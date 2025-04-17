package com.example.reinforcement.data.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleTask(
    val id: Int,
    val title: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    var isCompleted: Boolean = false,
    val completionDate: LocalDate? = null
)