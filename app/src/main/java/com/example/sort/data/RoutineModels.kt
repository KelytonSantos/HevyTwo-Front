package com.example.sort.data

data class RoutineRequest(
    val routineName: String
)

data class RoutineResponse(
    val id: String
)

data class RoutineWorkoutResponse(
    val id: String
)

data class RoutineItem(
    val routineId: String,
    val routineName: String,
    val totalWorkouts: Int
)

data class RoutineListResponse(
    val userId: String,
    val totalRoutines: Int,
    val routines: List<RoutineItem>
)

// --- Edit Routine: real API response models ---

/** Response from GET /workouts/my/routine/{routineId} */
data class RoutineWorkoutItem(
    val id: String,                        // this is the routineWorkoutId
    val workoutName: String? = null,
    val workoutImage: String? = null,
    val exerciseApiId: String? = null,
    val description: String? = null,
    val orderIndex: Int? = null,
    val restTimeSeconds: Int? = null
)

/** Response from GET /workouts/routine/workout/{routineWorkoutId}/set */
data class RoutineWorkoutSetDto(
    val id: String,
    val setType: String? = null,
    val measure: Double? = null,
    val unit: String? = null,
    val repetitions: Int? = null,
    val orderIndex: Int = 0,
    val restTime: Int? = null
)

/** Request body for POST /workouts/routine/workout/{routineWorkoutId}/set */
data class RoutineWorkoutSetRequest(
    val setType: String? = "NORMAL_SET",
    val measure: Double? = null,
    val unit: String? = "kg",
    val repetitions: Int? = null,
    val orderIndex: Int,
    val restTime: Int? = null
)

/** Request body for PATCH /workouts/routine/workout/set/{routineWorkoutSetId} */
data class RoutineWorkoutSetUpdateRequest(
    val measure: Double? = null,
    val repetitions: Int? = null,
    val restTime: Int? = null
)

// --- Mutable local state for editing ---

data class EditableSet(
    val setNumber: Int,
    val weight: String = "",
    val reps: String = "",
    val isCompleted: Boolean = false,
    /** Non-null if this set was loaded from the backend (already saved) */
    val existingId: String? = null
)

data class EditableExercise(
    val workoutId: String,
    val exerciseName: String,
    val workoutImage: String? = null,
    val restTime: String = "01:30",
    val sets: List<EditableSet> = listOf(EditableSet(setNumber = 1)),
    val exerciseApiId: String? = null
)

// ─── Active Workout (execution) models ──────────────────────────────────────

/** Returned by POST /routines/init/{routineId} */
data class RoutineExecution(
    val id: String
)

/** One item returned by GET /workouts/my/{routineExecutionId} */
data class WorkoutLogItem(
    val id: String,
    val workoutName: String? = null,
    val workoutImage: String? = null,
    val routineWorkoutId: String? = null,
    val orderIndex: Int? = null
)

/** Returned by POST /workouts/init/{workoutLogId} */
data class WorkoutSetResult(
    val id: String
)

/** Request body for POST /workouts/init/{workoutLogId} */
data class WorkoutSetInitRequest(
    val unit: String = "kg",
    val rep: Int,
    val orderIndex: Int,
    val measure: Double,
    val type: String = "NORMAL_SET",
    val restTime: Int = 60
)

/** Mutable set state during an active workout session */
data class ActiveSet(
    val orderIndex: Int,
    val previousKg: String = "",
    val previousReps: String = "",
    val kg: String = "",
    val reps: String = "",
    val workoutSetId: String? = null,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false
)

/** Mutable exercise state during an active workout session */
data class ActiveExercise(
    val workoutLogId: String,
    val exerciseName: String,
    val sets: List<ActiveSet> = emptyList()
)
