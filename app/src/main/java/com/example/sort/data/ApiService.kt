package com.example.sort.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {
    @POST("/auth/login") // O endpoint da sua API
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("/workouts/api/exercise/bd")
    suspend fun getExercises(@Query("offset") offset: Int = 0): List<com.example.sort.data.ExerciseDto>
}