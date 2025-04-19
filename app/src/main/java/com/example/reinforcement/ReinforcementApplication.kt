package com.example.reinforcement

import android.app.Application
import android.util.Log
import com.example.reinforcement.data.repository.CigarettesRepository
import com.example.reinforcement.data.repository.DailyGoalsRepository
import com.example.reinforcement.data.repository.MotivationalPhraseRepository
import com.example.reinforcement.data.repository.PointsRepository
import com.example.reinforcement.data.repository.PreferenceManager
import com.example.reinforcement.data.repository.ResetManager
import com.example.reinforcement.data.repository.ScheduleRepository
import com.example.reinforcement.data.repository.TodoRepository

class ReinforcementApplication : Application() {
    
    // Lazy initialization de los repositorios
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
    val resetManager by lazy { ResetManager(applicationContext) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        try {
            // Verificar si se necesita reseteo
            resetManager.checkAndResetIfNeeded()
            
            // Intentar programar el próximo reseteo
            try {
                resetManager.scheduleReset()
            } catch (e: Exception) {
                Log.e(TAG, "Error al programar el reseteo diario: ${e.message}")
                // Continuar con la inicialización aunque no podamos programar alarmas
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en la inicialización: ${e.message}")
        }
    }
    
    // Método para limpiar todos los datos (útil para pruebas o reinicio)
    fun clearAllData() {
        preferenceManager.clearAllPreferences()
    }
    
    companion object {
        private const val TAG = "ReinforcementApp"
        private lateinit var instance: ReinforcementApplication
        
        fun getInstance(): ReinforcementApplication {
            return instance
        }
    }
}