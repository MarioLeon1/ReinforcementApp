package com.example.reinforcement.data.repository

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PreferenceManager(context: Context) {
    
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    companion object {
        private const val PREFS_NAME = "reinforcement_preferences"
        
        // Clave para Objetivos Diarios
        private const val KEY_DAILY_GOAL_PREFIX = "daily_goal_"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
        
        // Clave para Horario
        private const val KEY_SCHEDULE_TASK_PREFIX = "schedule_task_"
        
        // Claves para To-Do
        private const val KEY_TODO_TASK_PREFIX = "todo_task_"
        private const val KEY_TODO_TASK_IDS = "todo_task_ids"
        
        // Clave para Cigarrillos
        private const val KEY_CIGARETTE_COUNT_PREFIX = "cigarette_count_"
    }
    
    // ----- MÉTODOS PARA OBJETIVOS DIARIOS -----
    
    fun saveGoalCompletionState(goalId: Int, isCompleted: Boolean) {
        preferences.edit().putBoolean("$KEY_DAILY_GOAL_PREFIX$goalId", isCompleted).apply()
    }
    
    fun getGoalCompletionState(goalId: Int): Boolean {
        return preferences.getBoolean("$KEY_DAILY_GOAL_PREFIX$goalId", false)
    }
    
    fun saveLastResetDate(date: LocalDate) {
        preferences.edit().putString(KEY_LAST_RESET_DATE, date.format(dateFormatter)).apply()
    }
    
    fun getLastResetDate(): LocalDate? {
        val dateString = preferences.getString(KEY_LAST_RESET_DATE, null)
        return if (dateString != null) {
            LocalDate.parse(dateString, dateFormatter)
        } else {
            null
        }
    }
    
    // ----- MÉTODOS PARA HORARIO -----
    
    fun saveScheduleTaskCompletionState(taskId: Int, date: LocalDate, isCompleted: Boolean) {
        val key = "$KEY_SCHEDULE_TASK_PREFIX${taskId}_${date.format(dateFormatter)}"
        preferences.edit().putBoolean(key, isCompleted).apply()
    }
    
    fun getScheduleTaskCompletionState(taskId: Int, date: LocalDate): Boolean {
        val key = "$KEY_SCHEDULE_TASK_PREFIX${taskId}_${date.format(dateFormatter)}"
        return preferences.getBoolean(key, false)
    }
    
    // ----- MÉTODOS PARA TO-DO -----
    
    fun saveTodoTasksIds(taskIds: Set<String>) {
        preferences.edit().putStringSet(KEY_TODO_TASK_IDS, taskIds).apply()
    }
    
    fun getTodoTasksIds(): Set<String> {
        return preferences.getStringSet(KEY_TODO_TASK_IDS, emptySet()) ?: emptySet()
    }
    
    fun saveTodoTask(taskId: Int, title: String, date: LocalDate, isCompleted: Boolean) {
        val keyPrefix = "$KEY_TODO_TASK_PREFIX$taskId"
        
        preferences.edit().apply {
            putString("${keyPrefix}_title", title)
            putString("${keyPrefix}_date", date.format(dateFormatter))
            putBoolean("${keyPrefix}_completed", isCompleted)
            apply()
        }
        
        // Actualizar la lista de IDs
        val taskIds = getTodoTasksIds().toMutableSet()
        taskIds.add(taskId.toString())
        saveTodoTasksIds(taskIds)
    }
    
    fun getTodoTaskTitle(taskId: Int): String? {
        return preferences.getString("${KEY_TODO_TASK_PREFIX}${taskId}_title", null)
    }
    
    fun getTodoTaskDate(taskId: Int): LocalDate? {
        val dateString = preferences.getString("${KEY_TODO_TASK_PREFIX}${taskId}_date", null)
        return if (dateString != null) {
            LocalDate.parse(dateString, dateFormatter)
        } else {
            null
        }
    }
    
    fun getTodoTaskCompletionState(taskId: Int): Boolean {
        return preferences.getBoolean("${KEY_TODO_TASK_PREFIX}${taskId}_completed", false)
    }
    
    fun deleteTodoTask(taskId: Int) {
        val keyPrefix = "$KEY_TODO_TASK_PREFIX$taskId"
        
        preferences.edit().apply {
            remove("${keyPrefix}_title")
            remove("${keyPrefix}_date")
            remove("${keyPrefix}_completed")
            apply()
        }
        
        // Actualizar la lista de IDs
        val taskIds = getTodoTasksIds().toMutableSet()
        taskIds.remove(taskId.toString())
        saveTodoTasksIds(taskIds)
    }
    
    // ----- MÉTODOS PARA CIGARRILLOS -----
    
    fun saveCigaretteCount(date: LocalDate, count: Int) {
        val key = "$KEY_CIGARETTE_COUNT_PREFIX${date.format(dateFormatter)}"
        preferences.edit().putInt(key, count).apply()
    }
    
    fun getCigaretteCount(date: LocalDate): Int {
        val key = "$KEY_CIGARETTE_COUNT_PREFIX${date.format(dateFormatter)}"
        return preferences.getInt(key, 0)
    }
    
    // ----- MÉTODOS GENERALES -----
    
    fun clearAllPreferences() {
        preferences.edit().clear().apply()
    }
}