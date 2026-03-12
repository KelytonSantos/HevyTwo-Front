package com.example.sort.data

class AuthRepository(private val apiService: ApiService) {

    suspend fun realizarLogin(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun realizarCadastro(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(username, email, password)
            val response = apiService.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}