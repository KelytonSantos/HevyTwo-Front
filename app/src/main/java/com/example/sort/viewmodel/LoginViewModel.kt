package com.example.sort.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel // Mudamos de ViewModel para AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sort.data.AuthRepository
import com.example.sort.data.RetrofitClient
import com.example.sort.data.SessionManager
import kotlinx.coroutines.launch

// Agora recebemos o 'application' no construtor
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(RetrofitClient.instance)
    private val sessionManager = SessionManager(application)

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var username by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    // NOVA VARIÁVEL: Para a tela saber que deve navegar
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
                    // 1. Salva o Token e o nome no DataStore
                    sessionManager.saveSession(dadosUsuario.jwt, dadosUsuario.username)

                    // 2. Ativa o gatilho de navegação
                    isLoginSuccessful = true
                    println("LOGIN SUCESSO! Token salvo.")
                }
            } else {
                loginError = "Erro: ${resultado.exceptionOrNull()?.message}"
            }
            isLoading = false
        }
    }

    // 4. FUNÇÃO DE CADASTRO
    fun onRegisterClick() {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            loginError = "Preencha todos os campos para cadastrar"
            return
        }

        viewModelScope.launch {
            isLoading = true
            loginError = null

            // Aqui você chamaria repository.registrar(...) se tivesse criado essa função
            println("Simulando cadastro para: $username")

            isLoading = false
        }
    }
}