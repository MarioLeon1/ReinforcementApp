package com.example.reinforcement.data.repository

import com.example.reinforcement.data.model.DailyGoal
import com.example.reinforcement.data.model.GoalCategory
import java.time.LocalDate

class DailyGoalsRepository {
    private val defaultGoals = listOf(
        DailyGoal(1, GoalCategory.PHYSICAL, "Salir a correr"),
        DailyGoal(2, GoalCategory.PHYSICAL, "Ir al gimnasio"),
        DailyGoal(3, GoalCategory.MENTAL, "Leer"),
        DailyGoal(4, GoalCategory.MENTAL, "Escribir"),
        DailyGoal(5, GoalCategory.MENTAL, "Aprender algo nuevo"),
        DailyGoal(6, GoalCategory.DISCIPLINE, "Ir a clase"),
        DailyGoal(7, GoalCategory.DISCIPLINE, "Estudiar"),
        DailyGoal(8, GoalCategory.DISCIPLINE, "Empresa")
    )
    
    private var currentGoals = mutableListOf<DailyGoal>()
    private var lastResetDate: LocalDate = LocalDate.now()
    
    init {
        resetDailyGoals()
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
            return goal.isCompleted
        }
        return false
    }
    
    fun getCompletedGoalsCount(): Int {
        return currentGoals.count { it.isCompleted }
    }
    
    private fun checkAndResetIfNeeded() {
        val today = LocalDate.now()
        if (today.isAfter(lastResetDate)) {
            resetDailyGoals()
        }
    }
    
    private fun resetDailyGoals() {
        currentGoals = defaultGoals.map { it.copy(isCompleted = false) }.toMutableList()
        lastResetDate = LocalDate.now()
    }
}