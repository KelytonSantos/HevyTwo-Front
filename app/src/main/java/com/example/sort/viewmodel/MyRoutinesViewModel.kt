package com.example.sort.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sort.data.RetrofitClient
import com.example.sort.data.RoutineItem
import com.example.sort.data.RoutineRepository
import kotlinx.coroutines.launch

class MyRoutinesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RoutineRepository(RetrofitClient.getInstance(application))

    var routines by mutableStateOf<List<RoutineItem>>(emptyList())
        private set

    var totalRoutines by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun load() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.getRoutines()
                .onSuccess {
                    routines = it.routines
                    totalRoutines = it.totalRoutines
                }
                .onFailure {
                    errorMessage = it.message ?: "Erro ao carregar rotinas"
                }
            isLoading = false
        }
    }

    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
                .onSuccess {
                    routines = routines.filterNot { it.routineId == routineId }
                    totalRoutines = routines.size
                }
                .onFailure {
                    errorMessage = it.message ?: "Erro ao apagar rotina"
                }
        }
    }
}
