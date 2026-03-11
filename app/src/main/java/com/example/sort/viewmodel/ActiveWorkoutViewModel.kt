package com.example.sort.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sort.data.ActiveExercise
import com.example.sort.data.ActiveSet
import com.example.sort.data.RetrofitClient
import com.example.sort.data.RoutineRepository
import com.example.sort.data.WorkoutSetInitRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class ActiveWorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.getInstance(application)
    private val repository = RoutineRepository(api)

    var routineName by mutableStateOf("")
        private set

    var routineExecutionId by mutableStateOf<String?>(null)
        private set

    var exercises by mutableStateOf<List<ActiveExercise>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isFinishing by mutableStateOf(false)
        private set

    var isFinished by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // ── computed ──────────────────────────────────────────────────────────────

    val completedSets: Int
        get() = exercises.sumOf { ex -> ex.sets.count { it.isCompleted } }

    val totalVolume: Int
        get() = exercises.sumOf { ex ->
            ex.sets.filter { it.isCompleted }.sumOf { s ->
                val kg = s.kg.toDoubleOrNull() ?: 0.0
                val reps = s.reps.toIntOrNull() ?: 0
                (kg * reps).toInt()
            }
        }

    // ── init flow ─────────────────────────────────────────────────────────────

    /**
     * Full init sequence:
     *  1. Load template exercises + sets (for PREVIOUS values)
     *  2. POST /routines/init/{routineId}  → routineExecutionId
     *  3. GET /workouts/my/{routineExecutionId} → workoutLogs
     *  4. Build ActiveExercise list merging template + workoutLogs
     */
    fun initWorkout(routineId: String, name: String) {
        if (routineExecutionId != null) return // already initialised
        routineName = name
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1 — template exercises (parallel sets fetch)
                val templateMap = loadTemplateMap(routineId)

                // 2 — start execution
                val execution = api.initRoutineExecution(routineId)
                routineExecutionId = execution.id

                // 3 — workout logs
                val logs = api.getWorkoutLogs(execution.id)

                // 4 — build active exercises
                exercises = logs.mapIndexed { idx, log ->
                    // match by routineWorkoutId, then fall back to order index
                    val template = log.routineWorkoutId?.let { templateMap[it] }
                        ?: templateMap.values.elementAtOrNull(idx)

                    val sets = (template?.sets ?: listOf())
                        .mapIndexed { si, s ->
                            ActiveSet(
                                orderIndex = si + 1,
                                previousKg = s.weight,
                                previousReps = s.reps,
                                kg = s.weight,     // pre-fill from template
                                reps = s.reps
                            )
                        }
                        .ifEmpty { listOf(ActiveSet(orderIndex = 1)) }

                    ActiveExercise(
                        workoutLogId = log.id,
                        exerciseName = log.workoutName
                            ?: template?.exerciseName
                            ?: "Exercício ${idx + 1}",
                        sets = sets
                    )
                }
            } catch (e: Exception) {
                Log.e("ActiveWorkoutVM", "initWorkout failed: ${e.message}", e)
                errorMessage = e.message ?: "Erro ao iniciar treino"
            }
            isLoading = false
        }
    }

    /** Returns map of routineWorkoutId → EditableExercise (with template sets for PREVIOUS) */
    private suspend fun loadTemplateMap(routineId: String): Map<String, com.example.sort.data.EditableExercise> {
        val items = repository.getRoutineExercises(routineId).getOrNull() ?: return emptyMap()
        return supervisorScope {
            items.map { item ->
                async {
                    val setDtos = repository.getRoutineWorkoutSets(item.id)
                        .getOrNull()
                        ?.sortedBy { it.orderIndex }
                        ?: emptyList()
                    val editableSets = setDtos.mapIndexed { idx, dto ->
                        com.example.sort.data.EditableSet(
                            setNumber = idx + 1,
                            weight = dto.measure?.let { if (it > 0) it.toBigDecimal().stripTrailingZeros().toPlainString() else "" } ?: "",
                            reps = dto.repetitions?.let { if (it > 0) it.toString() else "" } ?: "",
                            existingId = dto.id
                        )
                    }.ifEmpty { listOf(com.example.sort.data.EditableSet(setNumber = 1)) }

                    item.id to com.example.sort.data.EditableExercise(
                        workoutId = item.id,
                        exerciseName = item.workoutName ?: "Exercício",
                        workoutImage = item.workoutImage,
                        sets = editableSets,
                        exerciseApiId = item.exerciseApiId
                    )
                }
            }.awaitAll().toMap()
        }
    }

    // ── set mutations ─────────────────────────────────────────────────────────

    fun updateSetKg(exerciseIndex: Int, setIndex: Int, value: String) {
        val ex = exercises[exerciseIndex]
        val newSets = ex.sets.toMutableList()
        newSets[setIndex] = newSets[setIndex].copy(kg = value)
        updateExercise(exerciseIndex, ex.copy(sets = newSets))
    }

    fun updateSetReps(exerciseIndex: Int, setIndex: Int, value: String) {
        val ex = exercises[exerciseIndex]
        val newSets = ex.sets.toMutableList()
        newSets[setIndex] = newSets[setIndex].copy(reps = value)
        updateExercise(exerciseIndex, ex.copy(sets = newSets))
    }

    /**
     * Toggle set completion:
     *  - Unchecked → POST init + POST finish (both API calls)
     *  - Checked   → just unmark locally (no cancel call for now)
     */
    fun toggleSet(exerciseIndex: Int, setIndex: Int) {
        val ex = exercises[exerciseIndex]
        val set = ex.sets[setIndex]

        if (set.isCompleted) {
            // Unmark locally
            updateSingleSet(exerciseIndex, setIndex, set.copy(isCompleted = false, workoutSetId = null))
            return
        }

        // Mark loading while API calls are in flight
        updateSingleSet(exerciseIndex, setIndex, set.copy(isLoading = true))

        viewModelScope.launch {
            try {
                val kg = set.kg.toDoubleOrNull() ?: 0.0
                val reps = set.reps.toIntOrNull() ?: 0

                // POST /workouts/init/{workoutLogId}
                val result = api.initWorkoutSet(
                    workoutLogId = ex.workoutLogId,
                    request = WorkoutSetInitRequest(
                        rep = reps,
                        orderIndex = set.orderIndex,
                        measure = kg,
                        type = "NORMAL_SET",
                        restTime = 60
                    )
                )

                // POST /workouts/set/{workoutSetId}/finish
                api.finishWorkoutSet(result.id)

                updateSingleSet(
                    exerciseIndex, setIndex,
                    set.copy(workoutSetId = result.id, isCompleted = true, isLoading = false)
                )
            } catch (e: Exception) {
                Log.e("ActiveWorkoutVM", "toggleSet failed: ${e.message}", e)
                errorMessage = "Erro ao salvar set: ${e.message}"
                updateSingleSet(exerciseIndex, setIndex, set.copy(isLoading = false))
            }
        }
    }

    fun addSet(exerciseIndex: Int) {
        val ex = exercises[exerciseIndex]
        val lastSet = ex.sets.lastOrNull()
        val newSet = ActiveSet(
            orderIndex = ex.sets.size + 1,
            previousKg = lastSet?.previousKg ?: "",
            previousReps = lastSet?.previousReps ?: "",
            kg = lastSet?.kg ?: "",
            reps = lastSet?.reps ?: ""
        )
        updateExercise(exerciseIndex, ex.copy(sets = ex.sets + newSet))
    }

    // ── finish workout ────────────────────────────────────────────────────────

    fun finishWorkout(onSuccess: () -> Unit) {
        val execId = routineExecutionId ?: return
        viewModelScope.launch {
            isFinishing = true
            try {
                api.finishRoutineExecution(execId)
                isFinished = true
                onSuccess()
            } catch (e: Exception) {
                Log.e("ActiveWorkoutVM", "finishWorkout failed: ${e.message}", e)
                errorMessage = "Erro ao finalizar treino: ${e.message}"
            }
            isFinishing = false
        }
    }

    fun cancelWorkout(onSuccess: () -> Unit) {
        val execId = routineExecutionId
        viewModelScope.launch {
            if (execId != null) {
                try {
                    api.cancelRoutineExecution(execId)
                } catch (e: Exception) {
                    Log.e("ActiveWorkoutVM", "cancelWorkout failed: ${e.message}", e)
                }
            }
            onSuccess()
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun updateExercise(index: Int, exercise: ActiveExercise) {
        exercises = exercises.toMutableList().also { it[index] = exercise }
    }

    private fun updateSingleSet(exerciseIndex: Int, setIndex: Int, set: ActiveSet) {
        val ex = exercises[exerciseIndex]
        val newSets = ex.sets.toMutableList().also { it[setIndex] = set }
        updateExercise(exerciseIndex, ex.copy(sets = newSets))
    }
}
