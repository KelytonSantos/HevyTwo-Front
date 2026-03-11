package com.example.sort.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sort.data.DashboardResponse
import com.example.sort.data.TimeAndDays
import com.example.sort.data.RetrofitClient
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.getInstance(application)

    var dashboard by mutableStateOf<DashboardResponse?>(null)
        private set

    var graph by mutableStateOf<List<TimeAndDays>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun load() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                dashboard = api.getDashboard()
                graph = api.getDashboardGraph().data
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro ao carregar dashboard"
            }
            isLoading = false
        }
    }
}
