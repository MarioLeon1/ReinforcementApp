package com.example.reinforcement

import android.app.Application
import com.example.reinforcement.data.repository.CigarettesRepository
import com.example.reinforcement.data.repository.DailyGoalsRepository
import com.example.reinforcement.data.repository.MotivationalPhraseRepository
import com.example.reinforcement.data.repository.PointsRepository
import com.example.reinforcement.data.repository.PreferenceManager
import com.example.reinforcement.data.repository.ScheduleRepository
import com.example.reinforcement.data.repository.TodoRepository

class ReinforcementApplication : Application() {
    
    // Inicialización lazy de los repositorios
    val preferenceManager by lazy { PreferenceManager(applicationContext) }
    val cigarettesRepository by lazy { CigarettesRepository(applicationContext) }
    val dailyGoalsRepository by lazy { DailyGoalsRepository(applicationContext) }
    val scheduleRepository by lazy { ScheduleRepository(applicationContext) }
    val todoRepository by lazy { TodoRepository(applicationContext) }
    val motivationalPhraseRepository by lazy { MotivationalPhraseRepository(applicationContext) }
    val pointsRepository by lazy { 
        PointsRepository(
            dailyGoalsRepository,
            scheduleRepository,
            todoRepository,
            cigarettesRepository
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    // Método para limpiar todos los datos (útil para pruebas o reinicio)
    fun clearAllData() {
        preferenceManager.clearAllPreferences()
    }
    
    companion object {
        private lateinit var instance: ReinforcementApplication
        
        fun getInstance(): ReinforcementApplication {
            return instance
        }
    }
}