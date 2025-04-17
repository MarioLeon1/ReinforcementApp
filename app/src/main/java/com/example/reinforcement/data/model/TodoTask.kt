package com.example.reinforcement.data.model

import java.time.LocalDate

data class TodoTask(
    val id: Int,
    val title: String,
    val date: LocalDate,
    var isCompleted: Boolean = false,
    val creationDate: LocalDate = LocalDate.now()
)