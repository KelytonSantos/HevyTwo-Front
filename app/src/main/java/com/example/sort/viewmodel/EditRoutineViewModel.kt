package com.example.sort.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sort.data.EditableExercise
import com.example.sort.data.EditableSet
import com.example.sort.data.RetrofitClient
import com.example.sort.data.RoutineRepository
import com.example.sort.data.RoutineWorkoutSetRequest
import com.example.sort.data.RoutineWorkoutSetUpdateRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class EditRoutineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RoutineRepository(RetrofitClient.getInstance(application))

    var routineId by mutableStateOf("")
        private set

    var routineName by mutableStateOf("")
        private set

    var exercises by mutableStateOf<List<EditableExercise>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    // --- Computed stats ---

    val totalSets: Int get() = exercises.sumOf { it.sets.size }

    val totalVolume: Double get() = exercises.sumOf { ex ->
        ex.sets.sumOf { s ->
            val w = s.weight.toDoubleOrNull() ?: 0.0
            val r = s.reps.toIntOrNull() ?: 0
            w * r
        }
    }

    // --- Load ---
    // Step 1: GET /workouts/my/routine/{routineId}  → list of exercises
    // Step 2: for each exercise, GET /workouts/routine/workout/{id}/set → pre-fill sets

    fun load(id: String, name: String) {
        routineId = id
        routineName = name
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.getRoutineExercises(id)
                .onSuccess { items ->
                    // Fetch sets for all exercises in parallel, isolated so one failure doesn't cancel others
                    val exercisesWithSets = supervisorScope {
                        items.map { item ->
                            async {
                                val setsResult = repository.getRoutineWorkoutSets(item.id)
                                val editableSets = setsResult.getOrNull()
                                    ?.sortedBy { it.orderIndex }
                                    ?.mapIndexed { idx, dto ->
                                        EditableSet(
                                            setNumber = idx + 1,
                                            weight = dto.measure?.let { if (it > 0) it.toBigDecimal().stripTrailingZeros().toPlainString() else "" } ?: "",
                                            reps = dto.repetitions?.let { if (it > 0) it.toString() else "" } ?: "",
                                            existingId = dto.id
                                        )
                                    }
                                    ?.takeIf { it.isNotEmpty() }
                                    ?: listOf(EditableSet(setNumber = 1))

                                // restTimeSeconds comes from the exercise item itself
                                val restSecs = item.restTimeSeconds ?: 90
                                EditableExercise(
                                    workoutId = item.id,
                                    exerciseName = item.workoutName ?: "Exercício",
                                    workoutImage = item.workoutImage,
                                    restTime = formatSeconds(restSecs),
                                    sets = editableSets
                                )
                            }
                        }.awaitAll()
                    }
                    exercises = exercisesWithSets
                }
                .onFailure {
                    Log.e("EditRoutineVM", "load failed: ${it.message}")
                    errorMessage = it.message ?: "Erro ao carregar rotina"
                }
            isLoading = false
        }
    }

    // --- Save ---
    // For sets with existingId  → PATCH /workouts/routine/workout/set/{id}
    // For sets without existingId → POST /workouts/routine/workout/{routineWorkoutId}/set

    fun saveRoutine() {
        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            var hasError = false

            for (exercise in exercises) {
                val restSecs = parseRestTime(exercise.restTime)

                for ((index, set) in exercise.sets.withIndex()) {
                    if (set.existingId != null) {
                        // Already saved → PATCH
                        val request = RoutineWorkoutSetUpdateRequest(
                            measure = set.weight.toDoubleOrNull(),
                            repetitions = set.reps.toIntOrNull(),
                            restTime = restSecs
                        )
                        repository.updateRoutineWorkoutSet(set.existingId, request)
                            .onFailure { e ->
                                Log.e("EditRoutineVM", "updateSet failed for ${exercise.exerciseName}: ${e.message}")
                                hasError = true
                                errorMessage = "Erro ao atualizar set: ${e.message}"
                            }
                    } else {
                        // New set → POST
                        val request = RoutineWorkoutSetRequest(
                            setType = "NORMAL_SET",
                            measure = set.weight.toDoubleOrNull(),
                            unit = "kg",
                            repetitions = set.reps.toIntOrNull(),
                            orderIndex = index,
                            restTime = restSecs
                        )
                        repository.createRoutineWorkoutSet(exercise.workoutId, request)
                            .onFailure { e ->
                                Log.e("EditRoutineVM", "createSet failed for ${exercise.exerciseName}: ${e.message}")
                                hasError = true
                                errorMessage = "Erro ao criar set: ${e.message}"
                            }
                            .onSuccess { dto ->
                                exercises = exercises.map { ex ->
                                    if (ex.workoutId == exercise.workoutId) {
                                        ex.copy(sets = ex.sets.mapIndexed { si, s ->
                                            if (si == index && s.existingId == null) s.copy(existingId = dto.id)
                                            else s
                                        })
                                    } else ex
                                }
                            }
                    }
                    if (hasError) break
                }
                if (hasError) break
            }

            if (!hasError) saveSuccess = true
            isSaving = false
        }
    }

    // --- Set editing ---

    fun updateSetWeight(exerciseIndex: Int, setIndex: Int, weight: String) {
        exercises = exercises.mapIndexed { ei, ex ->
            if (ei == exerciseIndex) {
                ex.copy(sets = ex.sets.mapIndexed { si, s ->
                    if (si == setIndex) s.copy(weight = weight) else s
                })
            } else ex
        }
    }

    fun updateSetReps(exerciseIndex: Int, setIndex: Int, reps: String) {
        exercises = exercises.mapIndexed { ei, ex ->
            if (ei == exerciseIndex) {
                ex.copy(sets = ex.sets.mapIndexed { si, s ->
                    if (si == setIndex) s.copy(reps = reps) else s
                })
            } else ex
        }
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int) {
        exercises = exercises.mapIndexed { ei, ex ->
            if (ei == exerciseIndex) {
                ex.copy(sets = ex.sets.mapIndexed { si, s ->
                    if (si == setIndex) s.copy(isCompleted = !s.isCompleted) else s
                })
            } else ex
        }
    }

    fun updateRestTime(exerciseIndex: Int, restTime: String) {
        exercises = exercises.mapIndexed { ei, ex ->
            if (ei == exerciseIndex) ex.copy(restTime = restTime) else ex
        }
    }

    fun addSet(exerciseIndex: Int) {
        exercises = exercises.mapIndexed { ei, ex ->
            if (ei == exerciseIndex) {
                val lastSet = ex.sets.lastOrNull()
                val newSetNumber = (ex.sets.maxOfOrNull { it.setNumber } ?: 0) + 1
                ex.copy(
                    sets = ex.sets + EditableSet(
                        setNumber = newSetNumber,
                        weight = lastSet?.weight ?: "",
                        reps = lastSet?.reps ?: "",
                        existingId = null // new — not yet saved
                    )
                )
            } else ex
        }
    }

    fun deleteSet(exerciseIndex: Int, setIndex: Int) {
        val exercise = exercises.getOrNull(exerciseIndex) ?: return
        val set = exercise.sets.getOrNull(setIndex) ?: return
        if (set.existingId == null) {
            // Not yet saved — just remove locally
            exercises = exercises.mapIndexed { ei, ex ->
                if (ei == exerciseIndex) ex.copy(sets = ex.sets.filterIndexed { si, _ -> si != setIndex })
                else ex
            }
            return
        }
        viewModelScope.launch {
            repository.deleteRoutineWorkoutSet(set.existingId)
                .onSuccess {
                    exercises = exercises.mapIndexed { ei, ex ->
                        if (ei == exerciseIndex) ex.copy(sets = ex.sets.filterIndexed { si, _ -> si != setIndex })
                        else ex
                    }
                }
                .onFailure { e ->
                    Log.e("EditRoutineVM", "deleteSet failed: ${e.message}")
                    errorMessage = "Erro ao deletar set: ${e.message}"
                }
        }
    }

    fun removeExercise(exerciseIndex: Int) {
        val exercise = exercises.getOrNull(exerciseIndex) ?: return
        viewModelScope.launch {
            repository.deleteRoutineWorkout(exercise.workoutId)
                .onSuccess {
                    exercises = exercises.filterIndexed { index, _ -> index != exerciseIndex }
                }
                .onFailure { e ->
                    Log.e("EditRoutineVM", "removeExercise failed: ${e.message}")
                    errorMessage = "Erro ao remover exercício: ${e.message}"
                }
        }
    }

    fun clearSaveState() {
        saveSuccess = false
        errorMessage = null
    }

    // --- Helpers ---

    private fun formatSeconds(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    /** Parse "mm:ss" string → total seconds */
    private fun parseRestTime(restTime: String): Int {
        val parts = restTime.split(":")
        return if (parts.size == 2) {
            val m = parts[0].toIntOrNull() ?: 0
            val s = parts[1].toIntOrNull() ?: 0
            m * 60 + s
        } else {
            restTime.toIntOrNull() ?: 90
        }
    }
}
