package com.example.sort.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response


interface ApiService {
    @POST("/auth/login") // O endpoint da sua API
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("/workouts/api/exercise/bd")
    suspend fun getExercises(@Query("offset") offset: Int = 0): List<com.example.sort.data.ExerciseDto>

    @POST("/routines")
    suspend fun createRoutine(@Body request: RoutineRequest): RoutineResponse

    @POST("/workouts/{exerciseId}/{routineId}")
    suspend fun addExerciseToRoutine(
        @Path("exerciseId") exerciseId: String,
        @Path("routineId") routineId: String
    ): RoutineWorkoutResponse

    @GET("/routines")
    suspend fun getRoutines(): RoutineListResponse

    /** All exercises of a routine — GET /workouts/my/routine/{routineId} */
    @GET("/workouts/my/routine/{routineId}")
    suspend fun getRoutineExercises(
        @Path("routineId") routineId: String
    ): List<RoutineWorkoutItem>

    /** Template sets of one exercise in a routine */
    @GET("/workouts/routine/workout/{routineWorkoutId}/set")
    suspend fun getRoutineWorkoutSets(
        @Path("routineWorkoutId") routineWorkoutId: String
    ): List<RoutineWorkoutSetDto>

    /** Save a template set for one exercise */
    @POST("/workouts/routine/workout/{routineWorkoutId}/set")
    suspend fun createRoutineWorkoutSet(
        @Path("routineWorkoutId") routineWorkoutId: String,
        @Body request: RoutineWorkoutSetRequest
    ): RoutineWorkoutSetDto

    @PATCH("/workouts/routine/workout/set/{routineWorkoutSetId}")
    suspend fun updateRoutineWorkoutSet(
        @Path("routineWorkoutSetId") routineWorkoutSetId: String,
        @Body request: RoutineWorkoutSetUpdateRequest
    ): RoutineWorkoutSetDto

    /** Delete a template set */
    @DELETE("/workouts/routine/workout/set/{routineWorkoutSetId}")
    suspend fun deleteRoutineWorkoutSet(
        @Path("routineWorkoutSetId") routineWorkoutSetId: String
    ): Response<Void>

    /** Remove an exercise from a routine */
    @DELETE("/workouts/routine/workout/{routineWorkoutId}")
    suspend fun deleteRoutineWorkout(
        @Path("routineWorkoutId") routineWorkoutId: String
    ): Response<Void>

    @DELETE("/routines/{routineId}")
    suspend fun deleteRoutine(@Path("routineId") routineId: String): Response<Void>
}