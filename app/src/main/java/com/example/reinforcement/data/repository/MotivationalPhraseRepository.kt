package com.example.reinforcement.data.repository

import android.content.Context
import com.example.reinforcement.R
import java.time.LocalDate

class MotivationalPhraseRepository(private val context: Context) {
    private var currentPhrase: String = ""
    private var lastPhraseDate: LocalDate = LocalDate.now().minusDays(1)
    
    fun getDailyPhrase(): String {
        val today = LocalDate.now()
        
        if (currentPhrase.isEmpty() || today.isAfter(lastPhraseDate)) {
            val phrases = context.resources.getStringArray(R.array.motivational_phrases)
            val randomIndex = today.dayOfYear % phrases.size
            currentPhrase = phrases[randomIndex]
            lastPhraseDate = today
        }
        
        return currentPhrase
    }
}