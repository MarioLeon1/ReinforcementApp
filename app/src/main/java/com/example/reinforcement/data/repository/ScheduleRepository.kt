package com.example.reinforcement.data.repository

import com.example.reinforcement.data.model.ScheduleTask
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class ScheduleRepository {
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
    
    // Mapa para guardar el estado de completado de las tareas por fecha
    private val completedTasksMap = mutableMapOf<Pair<Int, LocalDate>, Boolean>()
    
    // Obtener tareas para un día específico
    fun getTasksForDay(dayOfWeek: DayOfWeek): List<ScheduleTask> {
        val today = LocalDate.now()
        
        return defaultTasks
            .filter { it.dayOfWeek == dayOfWeek }
            .map { task ->
                val taskDate = today.with(dayOfWeek)
                val isCompleted = completedTasksMap[Pair(task.id, taskDate)] ?: false
                task.copy(isCompleted = isCompleted)
            }
            .sortedBy { it.startTime }
    }
    
    // Cambiar el estado de completado de una tarea
    fun toggleTaskCompletion(taskId: Int, date: LocalDate): Boolean {
        val key = Pair(taskId, date)
        val currentStatus = completedTasksMap[key] ?: false
        val newStatus = !currentStatus
        completedTasksMap[key] = newStatus
        return newStatus
    }
    
    // Comprobar si una tarea está completada
    fun isTaskCompleted(taskId: Int, date: LocalDate): Boolean {
        return completedTasksMap[Pair(taskId, date)] ?: false
    }
    
    // Obtener una tarea por su ID
    fun getTaskById(taskId: Int): ScheduleTask? {
        return defaultTasks.find { it.id == taskId }
    }
}