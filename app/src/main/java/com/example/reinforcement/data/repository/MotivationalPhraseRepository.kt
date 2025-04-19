package com.example.reinforcement.data.repository

import android.content.Context
import com.example.reinforcement.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MotivationalPhraseRepository(private val context: Context) {
    private val preferenceManager = PreferenceManager(context)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    companion object {
        private const val KEY_CURRENT_PHRASE = "current_motivational_phrase"
        private const val KEY_LAST_PHRASE_DATE = "last_phrase_date"
    }
    
    private var currentPhrase: String = ""
    private var lastPhraseDate: LocalDate = LocalDate.now().minusDays(1)
    
    init {
        // Cargar la frase y la fecha desde SharedPreferences
        loadSavedPhraseData()
    }
    
    private fun loadSavedPhraseData() {
        try {
            val preferences = context.getSharedPreferences("reinforcement_preferences", Context.MODE_PRIVATE)
            
            currentPhrase = preferences.getString(KEY_CURRENT_PHRASE, "") ?: ""
            
            val dateString = preferences.getString(KEY_LAST_PHRASE_DATE, null)
            if (dateString != null && dateString.isNotEmpty()) {
                // Remover cualquier comilla doble que pueda existir en la cadena
                val cleanDateString = dateString.replace("\"", "")
                lastPhraseDate = LocalDate.parse(cleanDateString, dateFormatter)
            } else {
                lastPhraseDate = LocalDate.now().minusDays(1)
            }
        } catch (e: Exception) {
            // Si hay algún error al cargar los datos, usar valores predeterminados
            currentPhrase = ""
            lastPhraseDate = LocalDate.now().minusDays(1)
        }
    }
    
    private fun saveCurrentPhraseData() {
        try {
            val preferences = context.getSharedPreferences("reinforcement_preferences", Context.MODE_PRIVATE)
            
            preferences.edit()
                .putString(KEY_CURRENT_PHRASE, currentPhrase)
                .putString(KEY_LAST_PHRASE_DATE, lastPhraseDate.format(dateFormatter))
                .apply()
        } catch (e: Exception) {
            // Si hay algún error al guardar, simplemente loguear y continuar
            // En una app real, deberías reportar esto a un servicio de crash reporting
        }
    }
    
    fun getDailyPhrase(): String {
        val today = LocalDate.now()
        
        if (currentPhrase.isEmpty() || today.isAfter(lastPhraseDate)) {
            val phrases = context.resources.getStringArray(R.array.motivational_phrases)
            val randomIndex = today.dayOfYear % phrases.size
            currentPhrase = phrases[randomIndex]
            lastPhraseDate = today
            
            // Guardar la frase actualizada
            saveCurrentPhraseData()
        }
        
        return currentPhrase
    }
}