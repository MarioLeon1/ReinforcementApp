package com.example.reinforcement.data.repository

import android.content.Context
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
        const val CATEGORY_COMBINATION_BONUS = 10
        const val MISSING_CATEGORY_PENALTY = -10
        const val ACADEMIC_GOAL_MISSED_PENALTY = -10

        const val TODO_MAX_POINTS = 50

        const val CIGARETTE_MAX_POINTS = 50
        const val CIGARETTE_LIMIT = 10
        const val CIGARETTE_PENALTY_PER_EXCESS = -10
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
        calculateDailyGoalsPoints(pointsData)

        // 2. Puntos por tareas del To-Do (proporcional, máximo 50 puntos)
        calculateTodoPoints(pointsData, date)

        // 3. Puntos (o penalizaciones) por cigarros
        calculateCigarettePoints(pointsData, date)

        // Actualizar el total de puntos
        pointsData.totalPoints = pointsData.pointsBreakdown.sumOf { it.points }

        return pointsData
    }

    private fun calculateDailyGoalsPoints(pointsData: PointsData) {
        val allGoals = dailyGoalsRepository.getAllGoals()
        val completedGoals = allGoals.filter { it.isCompleted }

        // Puntos por objetivos completados
        for (goal in completedGoals) {
            addPointEntry(pointsData, goal.title, DAILY_GOAL_POINTS, PointCategory.DAILY_GOAL)
        }

        // Comprobar si hay al menos un objetivo completado de cada categoría
        val hasPhysicalGoal = completedGoals.any { it.category == GoalCategory.PHYSICAL }
        val hasMentalGoal = completedGoals.any { it.category == GoalCategory.MENTAL }
        val hasDisciplineGoal = completedGoals.any { it.category == GoalCategory.DISCIPLINE }

        if (hasPhysicalGoal && hasMentalGoal && hasDisciplineGoal) {
            // Bonus por tener al menos un objetivo de cada categoría
            addPointEntry(
                pointsData,
                "Bonus: Objetivos de tres categorías distintas",
                CATEGORY_COMBINATION_BONUS,
                PointCategory.BONUS
            )
        } else {
            // Penalización por no tener al menos un objetivo de cada categoría
            addPointEntry(
                pointsData,
                "Penalización: Faltan objetivos de algunas categorías",
                MISSING_CATEGORY_PENALTY,
                PointCategory.PENALTY
            )
        }

        // Penalización adicional por cada objetivo académico (disciplina) no completado
        // Este bloque ahora está fuera del "else" para que siempre se ejecute
        val uncompletedAcademicGoals = allGoals
            .filter { it.category == GoalCategory.DISCIPLINE && !it.isCompleted }

        if (uncompletedAcademicGoals.isNotEmpty()) {
            for (goal in uncompletedAcademicGoals) {
                addPointEntry(
                    pointsData,
                    "Penalización: ${goal.title} no completado",
                    ACADEMIC_GOAL_MISSED_PENALTY,
                    PointCategory.PENALTY
                )
            }
        }
    }

    private fun calculateTodoPoints(pointsData: PointsData, date: LocalDate) {
        val todoTasks = todoRepository.getTasksForDate(date)

        if (todoTasks.isEmpty()) {
            return  // No hay tareas, no hay puntos
        }

        val completedTasks = todoTasks.count { it.isCompleted }
        val completionPercentage = completedTasks.toFloat() / todoTasks.size
        val earnedPoints = (TODO_MAX_POINTS * completionPercentage).toInt()

        if (earnedPoints > 0) {
            addPointEntry(
                pointsData,
                "$completedTasks de ${todoTasks.size} tareas completadas",
                earnedPoints,
                PointCategory.TODO
            )
        }
    }

    private fun calculateCigarettePoints(pointsData: PointsData, date: LocalDate) {
        val cigaretteData = cigarettesRepository.getCigaretteData(date)
        val cigaretteCount = cigaretteData.count

        if (cigaretteCount == 0) {
            // No ha fumado ningún cigarro
            addPointEntry(
                pointsData,
                "No has fumado ningún cigarro",
                CIGARETTE_MAX_POINTS,
                PointCategory.CIGARETTES
            )
        } else if (cigaretteCount <= CIGARETTE_LIMIT) {
            // Ha fumado dentro del límite
            val pointsLost = (cigaretteCount.toFloat() / CIGARETTE_LIMIT * CIGARETTE_MAX_POINTS).toInt()
            val earnedPoints = CIGARETTE_MAX_POINTS - pointsLost

            addPointEntry(
                pointsData,
                "$cigaretteCount cigarros fumados",
                earnedPoints,
                PointCategory.CIGARETTES
            )
        } else {
            // Ha excedido el límite
            val excessCigarettes = cigaretteCount - CIGARETTE_LIMIT
            val penaltyPoints = excessCigarettes * CIGARETTE_PENALTY_PER_EXCESS

            addPointEntry(
                pointsData,
                "$cigaretteCount cigarros fumados (exceso de $excessCigarettes)",
                penaltyPoints,
                PointCategory.CIGARETTES
            )
        }
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