package com.example.reinforcement.data.repository

import android.content.Context
import com.example.reinforcement.data.model.ScheduleTask
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class ScheduleRepository(context: Context) {
    private val preferenceManager = PreferenceManager(context)
    
    // Ejemplo de tareas del horario (en una aplicación real, estas vendrían de una base de datos)
    private val defaultTasks = listOf(
        // LUNES
        ScheduleTask(
            id = 101,
            title = "Despertarse",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(8, 30)
        ),
        ScheduleTask(
            id = 102,
            title = "Clase: Redes y Sistemas",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(12, 0)
        ),
        ScheduleTask(
            id = 103,
            title = "Hora de comer",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0)
        ),
        ScheduleTask(
            id = 104,
            title = "Estar en la empresa",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(15, 0),
            endTime = LocalTime.of(18, 0)
        ),
        ScheduleTask(
            id = 105,
            title = "Estudiar",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(18, 30),
            endTime = LocalTime.of(20, 0)
        ),
        
        // MARTES
        ScheduleTask(
            id = 201,
            title = "Despertarse",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(8, 30)
        ),
        ScheduleTask(
            id = 202,
            title = "Clase: Desarrollo de Software",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(14, 0)
        ),
        ScheduleTask(
            id = 203,
            title = "Hora de comer",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0)
        ),
        ScheduleTask(
            id = 204,
            title = "Gimnasio",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(17, 0),
            endTime = LocalTime.of(18, 30)
        ),
        
        // MIÉRCOLES
        ScheduleTask(
            id = 301,
            title = "Despertarse",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(8, 30)
        ),
        ScheduleTask(
            id = 302,
            title = "Clase: Ciberseguridad",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(11, 0)
        ),
        ScheduleTask(
            id = 303,
            title = "Proyecto grupal",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(12, 0),
            endTime = LocalTime.of(14, 0)
        ),
        
        // JUEVES
        ScheduleTask(
            id = 401,
            title = "Despertarse",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(8, 30)
        ),
        ScheduleTask(
            id = 402,
            title = "Clase: Programación",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(13, 0)
        ),
        
        // VIERNES
        ScheduleTask(
            id = 501,
            title = "Despertarse",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(8, 30)
        ),
        ScheduleTask(
            id = 502,
            title = "Tutoría",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0)
        ),
        ScheduleTask(
            id = 503,
            title = "Estar en la empresa",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(12, 0),
            endTime = LocalTime.of(18, 0)
        ),
        
        // SÁBADO
        ScheduleTask(
            id = 601,
            title = "Despertarse",
            dayOfWeek = DayOfWeek.SATURDAY,
            startTime = LocalTime.of(9, 30),
            endTime = LocalTime.of(10, 0)
        ),
        ScheduleTask(
            id = 602,
            title = "Salir a correr",
            dayOfWeek = DayOfWeek.SATURDAY,
            startTime = LocalTime.of(10, 30),
            endTime = LocalTime.of(11, 30)
        ),
        
        // DOMINGO
        ScheduleTask(
            id = 701,
            title = "Despertarse",
            dayOfWeek = DayOfWeek.SUNDAY,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 30)
        ),
        ScheduleTask(
            id = 702,
            title = "Preparar la semana",
            dayOfWeek = DayOfWeek.SUNDAY,
            startTime = LocalTime.of(19, 0),
            endTime = LocalTime.of(20, 0)
        )
    )
    
    // Obtener tareas para un día específico
    fun getTasksForDay(dayOfWeek: DayOfWeek): List<ScheduleTask> {
        val today = LocalDate.now()
        
        return defaultTasks
            .filter { it.dayOfWeek == dayOfWeek }
            .map { task ->
                val taskDate = today.with(dayOfWeek)
                val isCompleted = preferenceManager.getScheduleTaskCompletionState(task.id, taskDate)
                task.copy(isCompleted = isCompleted)
            }
            .sortedBy { it.startTime }
    }
    
    // Obtener tareas para un día específico en una fecha específica (útil para ver días anteriores)
    fun getTasksForDayAndDate(dayOfWeek: DayOfWeek, date: LocalDate): List<ScheduleTask> {
        return defaultTasks
            .filter { it.dayOfWeek == dayOfWeek }
            .map { task ->
                val isCompleted = preferenceManager.getScheduleTaskCompletionState(task.id, date)
                task.copy(isCompleted = isCompleted)
            }
            .sortedBy { it.startTime }
    }
    
    // Cambiar el estado de completado de una tarea
    fun toggleTaskCompletion(taskId: Int, date: LocalDate): Boolean {
        val currentStatus = preferenceManager.getScheduleTaskCompletionState(taskId, date)
        val newStatus = !currentStatus
        preferenceManager.saveScheduleTaskCompletionState(taskId, date, newStatus)
        return newStatus
    }
    
    // Comprobar si una tarea está completada
    fun isTaskCompleted(taskId: Int, date: LocalDate): Boolean {
        return preferenceManager.getScheduleTaskCompletionState(taskId, date)
    }
    
    // Obtener una tarea por su ID
    fun getTaskById(taskId: Int): ScheduleTask? {
        return defaultTasks.find { it.id == taskId }
    }
    
    // Mover las tareas completadas del día actual al día anterior
    fun moveCompletedTasksToYesterday(yesterday: LocalDate) {
        val today = LocalDate.now()
        
        // Para cada día de la semana
        for (dayOfWeek in DayOfWeek.values()) {
            val tasks = defaultTasks.filter { it.dayOfWeek == dayOfWeek }
            
            // Si hoy es ese día de la semana, guardar el estado para ayer y resetear hoy
            if (today.dayOfWeek == dayOfWeek) {
                for (task in tasks) {
                    val isCompleted = preferenceManager.getScheduleTaskCompletionState(task.id, today)
                    
                    // Si estaba completada, guardar ese estado para ayer
                    if (isCompleted) {
                        preferenceManager.saveScheduleTaskCompletionState(task.id, yesterday, true)
                    }
                    
                    // Resetear el estado para hoy
                    preferenceManager.saveScheduleTaskCompletionState(task.id, today, false)
                }
            }
        }
    }
}