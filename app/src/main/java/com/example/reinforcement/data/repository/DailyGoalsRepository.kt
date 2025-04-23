package com.example.reinforcement.data.repository

import android.content.Context
import com.example.reinforcement.data.model.DailyGoal
import com.example.reinforcement.data.model.GoalCategory
import java.time.LocalDate

class DailyGoalsRepository(context: Context) {
    private val preferenceManager = PreferenceManager(context)
    
    private val defaultGoals = listOf(
        DailyGoal(1, GoalCategory.PHYSICAL, "Salir a correr"),
        DailyGoal(2, GoalCategory.PHYSICAL, "Ir al gimnasio"),
        DailyGoal(3, GoalCategory.MENTAL, "Leer"),
        DailyGoal(4, GoalCategory.MENTAL, "Hablar con tus padres"),
        DailyGoal(5, GoalCategory.MENTAL, "Pasar tiempo con tus amigos/novia"),
        DailyGoal(6, GoalCategory.DISCIPLINE, "Ir a clase"),
        DailyGoal(7, GoalCategory.DISCIPLINE, "Estudiar"),
        DailyGoal(8, GoalCategory.DISCIPLINE, "Empresa"),
        DailyGoal(9, GoalCategory.PHYSICAL, "Meditar"),
        DailyGoal(10, GoalCategory.DISCIPLINE, "Despertarse a la hora")
    )
    
    private var currentGoals = mutableListOf<DailyGoal>()
    private var lastResetDate: LocalDate = LocalDate.now()
    
    init {
        // Recuperar la última fecha de reinicio de las preferencias
        preferenceManager.getLastResetDate()?.let {
            lastResetDate = it
        }
        
        checkAndResetIfNeeded()
    }
    
    fun getAllGoals(): List<DailyGoal> {
        checkAndResetIfNeeded()
        return currentGoals
    }
    
    fun getGoalsByCategory(category: GoalCategory): List<DailyGoal> {
        checkAndResetIfNeeded()
        return currentGoals.filter { it.category == category }
    }
    
    fun toggleGoalCompletion(goalId: Int): Boolean {
        checkAndResetIfNeeded()
        val goalIndex = currentGoals.indexOfFirst { it.id == goalId }
        if (goalIndex != -1) {
            val goal = currentGoals[goalIndex]
            goal.isCompleted = !goal.isCompleted
            currentGoals[goalIndex] = goal
            
            // Guardar el estado en SharedPreferences
            preferenceManager.saveGoalCompletionState(goalId, goal.isCompleted)
            
            return goal.isCompleted
        }
        return false
    }
    
    fun getCompletedGoalsCount(): Int {
        return currentGoals.count { it.isCompleted }
    }

    private fun checkAndResetIfNeeded() {
        val today = LocalDate.now()
        // Volver a cargar la fecha desde las preferencias para asegurar que tenemos el valor más reciente
        preferenceManager.getLastResetDate()?.let {
            lastResetDate = it
        }

        // Solo resetear si es un nuevo día y no si la lista está vacía
        if (today.isAfter(lastResetDate)) {
            resetDailyGoals()
        } else if (currentGoals.isEmpty()) {
            // Si los objetivos están vacíos pero no es un nuevo día, cargar desde preferencias
            loadGoalsFromPreferences()
        }
    }

    // Nuevo método para cargar objetivos desde preferencias
    private fun loadGoalsFromPreferences() {
        currentGoals = defaultGoals.map { goal ->
            // Cargar el estado guardado desde SharedPreferences
            val isCompleted = preferenceManager.getGoalCompletionState(goal.id)
            goal.copy(isCompleted = isCompleted)
        }.toMutableList()
    }

    
    // Método público para resetear objetivos diarios
    fun resetDailyGoals() {
        // Crear nuevos objetivos basados en los predeterminados, todos sin completar
        currentGoals = defaultGoals.map { goal ->
            // Al resetear, siempre se ponen como no completados
            val newGoal = goal.copy(isCompleted = false)
            
            // Guardar el nuevo estado en SharedPreferences
            preferenceManager.saveGoalCompletionState(goal.id, false)
            
            newGoal
        }.toMutableList()
        
        lastResetDate = LocalDate.now()
        
        // Guardar la nueva fecha de reinicio
        preferenceManager.saveLastResetDate(lastResetDate)
    }
}