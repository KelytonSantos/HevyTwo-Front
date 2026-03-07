package com.example.sort.data

class ExerciseRepository(private val apiService: ApiService) {

    suspend fun getExercises(offset: Int): Result<List<ExerciseDto>> {
        return try {
            val response = apiService.getExercises(offset)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
