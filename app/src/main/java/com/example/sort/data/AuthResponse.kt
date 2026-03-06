package com.example.sort.data

import java.util.UUID

data class AuthResponse(
    val id: UUID,
    val username: String,
    val followers: Int,
    val following: Int,
    val workouts: Int,
    val profileImg: ByteArray?, // byte[] do Java vira ByteArray no Kotlin
    val createdAt: String,      // Instant geralmente vira String no JSON
    val jwt: String
)