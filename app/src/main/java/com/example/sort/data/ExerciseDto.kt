package com.example.sort.data

data class ExerciseDto(
    val exerciseId: String,
    val name: String,
    val gifUrl: String,
    val targetMuscles: List<String>,
    val instructions: List<String>
)
