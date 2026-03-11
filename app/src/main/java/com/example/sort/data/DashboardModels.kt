package com.example.sort.data

data class DashboardResponse(
    val workouts: Int,
    val surplusBalance: Int,
    val isSurplus: Boolean,
    val totalDuration: Int,
    val volume: Int,
    val totalSets: Int,
    val topUsers: Int?,
    val avgHours: Double
)

data class GraphTimeOverMonthResponse(
    val data: List<TimeAndDays>
)

data class TimeAndDays(
    val days: Int,
    val hours: Double
)
