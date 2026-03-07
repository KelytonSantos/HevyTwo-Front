package com.example.sort.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sort.data.AuthRepository
import com.example.sort.data.RetrofitClient
import com.example.sort.data.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(RetrofitClient.getInstance(application))
    private val sessionManager = SessionManager(application)

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var username by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    var isLoginSuccessful by mutableStateOf(false)
        private set

    fun onLoginClick() {
        if (email.isBlank() || password.isBlank()) {
            loginError = "Campos vazios!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            loginError = null

            val resultado = repository.realizarLogin(email.trim(), password.trim())

            if (resultado.isSuccess) {
                val dadosUsuario = resultado.getOrNull()
                if (dadosUsuario != null) {
                    sessionManager.saveSession(dadosUsuario.jwt, dadosUsuario.username)
                    isLoginSuccessful = true
                }
            } else {
                loginError = "Erro: ${resultado.exceptionOrNull()?.message}"
            }
            isLoading = false
        }
    }

    fun onRegisterClick() {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            loginError = "Preencha todos os campos para cadastrar"
            return
        }

        viewModelScope.launch {
            isLoading = true
            loginError = null
            // Lógica de cadastro aqui
            isLoading = false
        }
    }
}
