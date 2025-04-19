package com.example.reinforcement.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.reinforcement.ReinforcementApplication
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

/**
 * Gestor para programar y ejecutar reseteos diarios de datos a las 00:00
 */
class ResetManager(private val context: Context) {
    private val preferenceManager = PreferenceManager(context)
    
    companion object {
        private const val TAG = "ResetManager"
        const val RESET_ACTION = "com.example.reinforcement.DAILY_RESET"
        private const val REQUEST_CODE = 1234
        
        // Clave para la fecha del último reseteo
        private const val KEY_LAST_RESET_DATE = "last_daily_reset_date"
    }
    
    /**
     * Programa el reseteo para ejecutarse a la próxima medianoche
     */
    fun scheduleReset() {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            // Crear intent para el BroadcastReceiver
            val intent = Intent(context, DailyResetReceiver::class.java).apply {
                action = RESET_ACTION
            }
            
            // Crear PendingIntent
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            
            val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
            
            // Calcular la próxima medianoche (hora local)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            
            // Programar la alarma - usar setInexactRepeating en lugar de setExact
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            
            Log.d(TAG, "Reseteo diario programado para: ${LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault())}")
        } catch (e: Exception) {
            Log.e(TAG, "Error al programar el reseteo: ${e.message}")
        }
    }
    
    /**
     * Resetea todos los datos para el nuevo día
     */
    fun performDailyReset() {
        Log.d(TAG, "Ejecutando reseteo diario")
        
        try {
            val app = context.applicationContext as ReinforcementApplication
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            
            // 1. Resetear objetivos diarios
            val dailyGoalsRepository = app.dailyGoalsRepository
            dailyGoalsRepository.resetDailyGoals()
            
            // 2. Mover tareas del horario completadas al día anterior
            val scheduleRepository = app.scheduleRepository
            scheduleRepository.moveCompletedTasksToYesterday(yesterday)
            
            // 3. Guardar el estado de la fecha actual para las tareas de To-Do
            val todoRepository = app.todoRepository
            todoRepository.preserveTasksCompletionState(yesterday)
            
            // 4. Guardar la fecha del último reseteo
            saveLastResetDate(today)
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el reseteo diario: ${e.message}")
        }
    }
    
    /**
     * Verifica si se debe realizar un reseteo
     * @return true si se requiere un reseteo, false en caso contrario
     */
    fun checkAndResetIfNeeded(): Boolean {
        val today = LocalDate.now()
        val lastResetDate = getLastResetDate()
        
        if (lastResetDate == null || lastResetDate.isBefore(today)) {
            performDailyReset()
            return true
        }
        
        return false
    }
    
    /**
     * Guarda la fecha del último reseteo
     */
    private fun saveLastResetDate(date: LocalDate) {
        preferenceManager.saveDateValue(KEY_LAST_RESET_DATE, date)
    }
    
    /**
     * Obtiene la fecha del último reseteo
     */
    private fun getLastResetDate(): LocalDate? {
        return preferenceManager.getDateValue(KEY_LAST_RESET_DATE)
    }
}

/**
 * BroadcastReceiver que recibe la alarma para realizar el reseteo diario
 */
class DailyResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ResetManager.RESET_ACTION) {
            Log.d("DailyResetReceiver", "Alarma de reseteo diario recibida")
            
            val resetManager = ResetManager(context)
            resetManager.performDailyReset()
            
            // Programar la próxima alarma para el día siguiente
            resetManager.scheduleReset()
        }
    }
}