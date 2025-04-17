package com.example.reinforcement.data.model

import java.time.LocalDate

data class CigaretteData(
    val date: LocalDate,
    var count: Int = 0,
    val limit: Int = 10
)