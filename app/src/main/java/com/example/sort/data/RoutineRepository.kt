package com.example.sort.data

import android.util.Log
import retrofit2.HttpException

class RoutineRepository(private val apiService: ApiService) {

    suspend fun createRoutine(routineName: String): Result<RoutineResponse> {
        return try {
            val response = apiService.createRoutine(RoutineRequest(routineName))
            Result.success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "sem corpo"
            Log.e("RoutineRepo", "createRoutine HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("HTTP ${e.code()} ao criar rotina: $errorBody"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "createRoutine falha: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun addExerciseToRoutine(exerciseId: String, routineId: String): Result<RoutineWorkoutResponse> {
        return try {
            Log.d("RoutineRepo", "addExercise exerciseId=$exerciseId routineId=$routineId")
            val response = apiService.addExerciseToRoutine(exerciseId, routineId)
            Result.success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "sem corpo"
            Log.e("RoutineRepo", "addExercise HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "addExercise falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }

    suspend fun getRoutines(): Result<RoutineListResponse> {
        return try {
            Result.success(apiService.getRoutines())
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "sem corpo"
            Log.e("RoutineRepo", "getRoutines HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "getRoutines falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }

    suspend fun getRoutineExercises(routineId: String): Result<List<RoutineWorkoutItem>> {
        return try {
            Result.success(apiService.getRoutineExercises(routineId))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "sem corpo"
            Log.e("RoutineRepo", "getRoutineExercises HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "getRoutineExercises falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }

    suspend fun getRoutineWorkoutSets(routineWorkoutId: String): Result<List<RoutineWorkoutSetDto>> {
        return try {
            Result.success(apiService.getRoutineWorkoutSets(routineWorkoutId))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "sem corpo"
            Log.e("RoutineRepo", "getRoutineWorkoutSets HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "getRoutineWorkoutSets falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }

    suspend fun createRoutineWorkoutSet(
        routineWorkoutId: String,
        request: RoutineWorkoutSetRequest
    ): Result<RoutineWorkoutSetDto> {
        return try {
            Result.success(apiService.createRoutineWorkoutSet(routineWorkoutId, request))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "sem corpo"
            Log.e("RoutineRepo", "createRoutineWorkoutSet HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "createRoutineWorkoutSet falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }

    suspend fun updateRoutineWorkoutSet(
        routineWorkoutSetId: String,
        request: RoutineWorkoutSetUpdateRequest
    ): Result<RoutineWorkoutSetDto> {
        return try {
            Result.success(apiService.updateRoutineWorkoutSet(routineWorkoutSetId, request))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "sem corpo"
            Log.e("RoutineRepo", "updateRoutineWorkoutSet HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "updateRoutineWorkoutSet falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }

    suspend fun deleteRoutineWorkoutSet(routineWorkoutSetId: String): Result<Unit> {
        return try {
            val response = apiService.deleteRoutineWorkoutSet(routineWorkoutSetId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("HTTP ${response.code()}"))
        } catch (e: HttpException) {
            Log.e("RoutineRepo", "deleteSet HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            Result.failure(Exception("HTTP ${e.code()}"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "deleteSet falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }

    suspend fun deleteRoutineWorkout(routineWorkoutId: String): Result<Unit> {
        return try {
            val response = apiService.deleteRoutineWorkout(routineWorkoutId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("HTTP ${response.code()}"))
        } catch (e: HttpException) {
            Log.e("RoutineRepo", "deleteWorkout HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            Result.failure(Exception("HTTP ${e.code()}"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "deleteWorkout falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }

    suspend fun deleteRoutine(routineId: String): Result<Unit> {
        return try {
            val response = apiService.deleteRoutine(routineId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "sem corpo"
                Log.e("RoutineRepo", "deleteRoutine HTTP ${response.code()}: $errorBody")
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "sem corpo"
            Log.e("RoutineRepo", "deleteRoutine HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("RoutineRepo", "deleteRoutine falha: ${e.message}")
            Result.failure(Exception(e.message ?: "Erro desconhecido"))
        }
    }
}
