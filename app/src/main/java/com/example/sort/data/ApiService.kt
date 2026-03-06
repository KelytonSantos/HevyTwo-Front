package com.example.sort.data

import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("/auth/login") // O endpoint da sua API
    suspend fun login(@Body request: LoginRequest): AuthResponse
}