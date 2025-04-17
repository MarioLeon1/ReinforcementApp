package com.example.reinforcement.data.repository

import com.example.reinforcement.data.model.DailyGoal
import com.example.reinforcement.data.model.GoalCategory
import com.example.reinforcement.data.model.PointCategory
import com.example.reinforcement.data.model.PointEntry
import com.example.reinforcement.data.model.PointsData
import com.example.reinforcement.data.model.ScheduleTask
import com.example.reinforcement.data.model.TodoTask
import java.time.LocalDate

class PointsRepository(
    private val dailyGoalsRepository: DailyGoalsRepository,
    private val scheduleRepository: ScheduleRepository,
    private val todoRepository: TodoRepository,
    private val cigarettesRepository: CigarettesRepository
) {
    
    private val pointsDataMap = mutableMapOf<LocalDate, PointsData>()
    
    // Constantes para la asignación de puntos
    companion object {
        const val DAILY_GOAL_POINTS = 10
        const val SCHEDULE_TASK_POINTS = 15
        const val TODO_TASK_POINTS = 10
        const val CIGARETTE_LIMIT = 10
        const val CIGARETTE_PENALTY = -10
        const val ALL_CATEGORIES_BONUS = 10
        const val PERFECT_SCHEDULE_BONUS = 50
    }
    
    // Obtener o crear datos de puntos para una fecha
    private fun getOrCreatePointsData(date: LocalDate): PointsData {
        if (!pointsDataMap.containsKey(date)) {
            pointsDataMap[date] = PointsData(date)
        }
        return pointsDataMap[date]!!
    }
    
    // Calcular puntos para una fecha específica
    fun calculatePointsForDate(date: LocalDate): PointsData {
        val pointsData = getOrCreatePointsData(date)
        
        // Limpiar entradas anteriores
        pointsData.pointsBreakdown.clear()
        
        // 1. Puntos por objetivos diarios
        val completedGoals = dailyGoalsRepository.getAllGoals().filter { it.isCompleted }
        for (goal in completedGoals) {
            addPointEntry(pointsData, goal.title, DAILY_GOAL_POINTS, PointCategory.DAILY_GOAL)
        }
        
        // 2. Puntos por tareas del horario
        val dayOfWeek = date.dayOfWeek
        val completedScheduleTasks = scheduleRepository.getTasksForDay(dayOfWeek).filter { it.isCompleted }
        for (task in completedScheduleTasks) {
            addPointEntry(pointsData, task.title, SCHEDULE_TASK_POINTS, PointCategory.SCHEDULE)
        }
        
        // 3. Puntos por tareas de To-Do
        val completedTodoTasks = todoRepository.getTasksForDate(date).filter { it.isCompleted }
        for (task in completedTodoTasks) {
            addPointEntry(pointsData, task.title, TODO_TASK_POINTS, PointCategory.TODO)
        }
        
        // 4. Puntos (o penalizaciones) por cigarros
        val cigaretteData = cigarettesRepository.getCigaretteData(date)
        if (cigaretteData.count > CIGARETTE_LIMIT) {
            val excessCigarettes = cigaretteData.count - CIGARETTE_LIMIT
            val penaltyPoints = excessCigarettes * CIGARETTE_PENALTY
            addPointEntry(
                pointsData,
                "$excessCigarettes cigarros más de los permitidos",
                penaltyPoints,
                PointCategory.CIGARETTES
            )
        } else if (cigaretteData.count <= CIGARETTE_LIMIT) {
            val savedCigarettes = CIGARETTE_LIMIT - cigaretteData.count
            val bonusPoints = savedCigarettes * 5  // 5 puntos por cada cigarro menos del límite
            if (bonusPoints > 0) {
                addPointEntry(
                    pointsData,
                    "Has fumado menos del límite",
                    bonusPoints,
                    PointCategory.CIGARETTES
                )
            }
        }
        
        // 5. Bonificación por cumplir objetivos en todas las categorías
        val hasPhysicalGoal = completedGoals.any { it.category == GoalCategory.PHYSICAL }
        val hasMentalGoal = completedGoals.any { it.category == GoalCategory.MENTAL }
        val hasDisciplineGoal = completedGoals.any { it.category == GoalCategory.DISCIPLINE }
        
        if (hasPhysicalGoal && hasMentalGoal && hasDisciplineGoal) {
            addPointEntry(
                pointsData,
                "Bonus: Objetivos de tres categorías distintas",
                ALL_CATEGORIES_BONUS,
                PointCategory.BONUS
            )
        }
        
        // 6. Bonificación por horario perfecto
        val allScheduleTasks = scheduleRepository.getTasksForDay(dayOfWeek)
        if (allScheduleTasks.isNotEmpty() && allScheduleTasks.all { it.isCompleted }) {
            addPointEntry(
                pointsData,
                "Bonus: Horario cumplido a la perfección",
                PERFECT_SCHEDULE_BONUS,
                PointCategory.BONUS
            )
        }
        
        // Actualizar el total de puntos
        pointsData.totalPoints = pointsData.pointsBreakdown.sumOf { it.points }
        
        return pointsData
    }
    
    private fun addPointEntry(
        pointsData: PointsData,
        description: String,
        points: Int,
        category: PointCategory
    ) {
        pointsData.pointsBreakdown.add(
            PointEntry(
                description = description,
                points = points,
                category = category
            )
        )
    }
    
    // Obtener historial de puntos para un rango de fechas
    fun getPointsHistory(startDate: LocalDate, endDate: LocalDate): List<PointsData> {
        val result = mutableListOf<PointsData>()
        var currentDate = startDate
        
        while (!currentDate.isAfter(endDate)) {
            result.add(calculatePointsForDate(currentDate))
            currentDate = currentDate.plusDays(1)
        }
        
        return result
    }
}