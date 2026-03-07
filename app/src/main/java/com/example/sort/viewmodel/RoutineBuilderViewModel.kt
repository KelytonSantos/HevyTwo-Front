package com.example.sort.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sort.data.ExerciseDto
import com.example.sort.data.RetrofitClient
import com.example.sort.data.RoutineRepository
import kotlinx.coroutines.launch

class RoutineBuilderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RoutineRepository(RetrofitClient.getInstance(application))

    var routineName by mutableStateOf("")
        private set

    var selectedExercises by mutableStateOf<List<ExerciseDto>>(emptyList())
        private set

    var isSaving by mutableStateOf(false)
        private set

    var saveError by mutableStateOf<String?>(null)
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    private var routineId by mutableStateOf<String?>(null)
    private var failedExerciseIds by mutableStateOf<List<String>>(emptyList())

    fun updateRoutineName(name: String) {
        routineName = name
        clearSaveState()
    }

    fun addExercise(exercise: ExerciseDto) {
        if (selectedExercises.none { it.exerciseId == exercise.exerciseId }) {
            selectedExercises = selectedExercises + exercise
            clearSaveState()
        }
    }

    fun removeExercise(exerciseId: String) {
        selectedExercises = selectedExercises.filterNot { it.exerciseId == exerciseId }
        failedExerciseIds = failedExerciseIds.filterNot { it == exerciseId }
        clearSaveState()
    }

    fun isSelected(exerciseId: String): Boolean {
        return selectedExercises.any { it.exerciseId == exerciseId }
    }

    fun reset() {
        routineName = ""
        selectedExercises = emptyList()
        failedExerciseIds = emptyList()
        routineId = null
        saveError = null
        saveSuccess = false
        isSaving = false
    }

    fun clearSaveState() {
        saveError = null
        saveSuccess = false
    }

    fun retryFailedExercises() {
        if (routineId == null || failedExerciseIds.isEmpty()) return
        saveRoutine(onlyFailed = true)
    }

    fun saveRoutine(onlyFailed: Boolean = false) {
        if (isSaving) return
        if (routineName.isBlank()) {
            saveError = "Informe o nome da rotina"
            return
        }
        if (selectedExercises.isEmpty()) {
            saveError = "Adicione pelo menos um exercicio"
            return
        }

        viewModelScope.launch {
            isSaving = true
            saveError = null
            saveSuccess = false

            val routineIdToUse = routineId ?: run {
                val createResult = repository.createRoutine(routineName.trim())
                createResult.getOrElse {
                    saveError = it.message ?: "Erro ao criar rotina"
                    isSaving = false
                    return@launch
                }.id
            }

            routineId = routineIdToUse

            val exercisesToSend = if (onlyFailed) {
                selectedExercises.filter { failedExerciseIds.contains(it.exerciseId) }
            } else {
                selectedExercises
            }

            val failures = mutableListOf<String>()
            exercisesToSend.forEach { exercise ->
                val result = repository.addExerciseToRoutine(exercise.exerciseId, routineIdToUse)
                if (result.isFailure) {
                    failures.add(exercise.exerciseId)
                }
            }

            failedExerciseIds = failures
            if (failures.isEmpty()) {
                saveSuccess = true
                routineId = null
            } else {
                saveError = "Falha ao adicionar ${failures.size} exercicio(s)"
            }

            isSaving = false
        }
    }

    fun getFailedExercises(): List<ExerciseDto> {
        return selectedExercises.filter { failedExerciseIds.contains(it.exerciseId) }
    }
}
