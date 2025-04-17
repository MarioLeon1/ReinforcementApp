package com.example.reinforcement.data.model

import java.time.LocalDate

data class PointsData(
    val date: LocalDate,
    var totalPoints: Int = 0,
    val pointsBreakdown: MutableList<PointEntry> = mutableListOf()
)

data class PointEntry(
    val description: String,
    val points: Int,
    val category: PointCategory
)

enum class PointCategory {
    DAILY_GOAL, SCHEDULE, TODO, CIGARETTES, BONUS
}