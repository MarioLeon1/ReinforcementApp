package com.example.reinforcement.data.model

import java.time.LocalDate

data class DailyGoal(
    val id: Int,
    val category: GoalCategory,
    val title: String,
    var isCompleted: Boolean = false,
    val completionDate: LocalDate? = null
)

enum class GoalCategory {
    PHYSICAL, MENTAL, DISCIPLINE
}