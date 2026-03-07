package com.example.sort.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sort.data.ExerciseDto
import com.example.sort.data.ExerciseRepository
import com.example.sort.data.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExploreExerciseViewModel(application: Application) : AndroidViewModel(application){

    private val repository = ExerciseRepository(RetrofitClient.getInstance(application))
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var exercises by mutableStateOf<List<ExerciseDto>>(emptyList())
        private set

    var isPendingLoad by mutableStateOf(false)
        private set

    private var offset = 0
    var endReached by mutableStateOf(false)
        private set
    private var lastPageRequestAt = 0L
    private val requestCooldownMs = 250L
    private var pendingJob: Job? = null

    fun loadFirstPage() {
        offset = 0
        endReached = false
        exercises = emptyList()
        loadNextPage()
    }

    fun loadNextPage() {
        val now = System.currentTimeMillis()
        if (isLoading || endReached) return

        val remainingCooldown = requestCooldownMs - (now - lastPageRequestAt)
        if (remainingCooldown > 0) {
            if (pendingJob == null) {
                isPendingLoad = true
                pendingJob = viewModelScope.launch {
                    delay(remainingCooldown)
                    pendingJob = null
                    isPendingLoad = false
                    loadNextPage()
                }
            }
            return
        }

        pendingJob?.cancel()
        pendingJob = null
        isPendingLoad = false
        lastPageRequestAt = now
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = repository.getExercises(offset)
            result
                .onSuccess { page ->
                    if (page.isEmpty()) {
                        endReached = true
                    } else {
                        exercises = exercises + page
                        offset += page.size
                    }
                }
                .onFailure { errorMessage = it.message ?: "Erro ao carregar exercicios" }

            isLoading = false
        }
    }
}
