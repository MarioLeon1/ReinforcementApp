package com.example.reinforcement.data.repository

import android.content.Context
import com.example.reinforcement.data.model.ScheduleTask
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class ScheduleRepository(context: Context) {
    private val preferenceManager = PreferenceManager(context)

    // Tareas del horario actualizado
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
            title = "Ir a la universidad",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 0)
        ),
        ScheduleTask(
            id = 103,
            title = "Clase: Bases de Datos Avanzadas (R2.10)",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(11, 0)
        ),
        ScheduleTask(
            id = 104,
            title = "Volver de la universidad",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(11, 30)
        ),
        ScheduleTask(
            id = 105,
            title = "Estudiar / Hacer trabajos",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30)
        ),
        ScheduleTask(
            id = 106,
            title = "Ir a por comida",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(12, 30),
            endTime = LocalTime.of(13, 0)
        ),
        ScheduleTask(
            id = 107,
            title = "Hora de comer",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(14, 0)
        ),
        ScheduleTask(
            id = 108,
            title = "Accenture: Horas laborales",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(19, 0)
        ),
        ScheduleTask(
            id = 109,
            title = "Objetivo físico",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(19, 0),
            endTime = LocalTime.of(19, 30)
        ),
        ScheduleTask(
            id = 110,
            title = "Tiempo solo",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(19, 30),
            endTime = LocalTime.of(20, 30)
        ),
        ScheduleTask(
            id = 111,
            title = "Hora de cenar",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(20, 30),
            endTime = LocalTime.of(21, 30)
        ),
        ScheduleTask(
            id = 112,
            title = "Tiempo con Irene/amigos",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(21, 30),
            endTime = LocalTime.of(23, 0)
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
            title = "Ir a la universidad",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 0)
        ),
        ScheduleTask(
            id = 203,
            title = "Clase: Redes y Sistemas Web (R2.08)",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(11, 0)
        ),
        ScheduleTask(
            id = 204,
            title = "Volver de la universidad",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(11, 30)
        ),
        ScheduleTask(
            id = 205,
            title = "Estudiar / Hacer trabajos",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(11, 30),
            endTime = LocalTime.of(12, 30)
        ),
        ScheduleTask(
            id = 206,
            title = "Ir a por comida",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(12, 30),
            endTime = LocalTime.of(13, 0)
        ),
        ScheduleTask(
            id = 207,
            title = "Hora de comer",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(14, 0)
        ),
        ScheduleTask(
            id = 208,
            title = "Accenture: Horas laborales",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(19, 0)
        ),
        ScheduleTask(
            id = 209,
            title = "Objetivo físico",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(19, 0),
            endTime = LocalTime.of(19, 30)
        ),
        ScheduleTask(
            id = 210,
            title = "Tiempo solo",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(19, 30),
            endTime = LocalTime.of(20, 30)
        ),
        ScheduleTask(
            id = 211,
            title = "Hora de cenar",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(20, 30),
            endTime = LocalTime.of(21, 30)
        ),
        ScheduleTask(
            id = 212,
            title = "Tiempo con Irene/amigos",
            dayOfWeek = DayOfWeek.TUESDAY,
            startTime = LocalTime.of(21, 30),
            endTime = LocalTime.of(23, 0)
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
            title = "Estudiar / Hacer trabajos",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(10, 30)
        ),
        ScheduleTask(
            id = 303,
            title = "Ir a la universidad",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(10, 30),
            endTime = LocalTime.of(11, 0)
        ),
        ScheduleTask(
            id = 304,
            title = "Clase: Desarrollo Software de Sistemas (R2.10)",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(13, 0)
        ),
        ScheduleTask(
            id = 305,
            title = "Hora de comer",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(14, 0)
        ),
        ScheduleTask(
            id = 306,
            title = "Accenture: Horas laborales",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(19, 0)
        ),
        ScheduleTask(
            id = 307,
            title = "Objetivo físico",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(19, 0),
            endTime = LocalTime.of(19, 30)
        ),
        ScheduleTask(
            id = 308,
            title = "Tiempo solo",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(19, 30),
            endTime = LocalTime.of(20, 30)
        ),
        ScheduleTask(
            id = 309,
            title = "Hora de cenar",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(20, 30),
            endTime = LocalTime.of(21, 30)
        ),
        ScheduleTask(
            id = 310,
            title = "Tiempo con Irene/amigos",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(21, 30),
            endTime = LocalTime.of(23, 0)
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
            title = "Ir a la universidad",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(9, 0)
        ),
        ScheduleTask(
            id = 403,
            title = "Clase: Ciberseguridad (L3.19)",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(13, 0)
        ),
        ScheduleTask(
            id = 404,
            title = "Hora de comer",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(14, 0)
        ),
        ScheduleTask(
            id = 405,
            title = "Accenture: Horas laborales",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(19, 0)
        ),
        ScheduleTask(
            id = 406,
            title = "Objetivo físico",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(19, 0),
            endTime = LocalTime.of(19, 30)
        ),
        ScheduleTask(
            id = 407,
            title = "Tiempo solo",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(19, 30),
            endTime = LocalTime.of(20, 30)
        ),
        ScheduleTask(
            id = 408,
            title = "Hora de cenar",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(20, 30),
            endTime = LocalTime.of(21, 30)
        ),
        ScheduleTask(
            id = 409,
            title = "Tiempo con Irene/amigos",
            dayOfWeek = DayOfWeek.THURSDAY,
            startTime = LocalTime.of(21, 30),
            endTime = LocalTime.of(23, 0)
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
            title = "Estudiar / Hacer trabajos",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(8, 30),
            endTime = LocalTime.of(10, 30)
        ),
        ScheduleTask(
            id = 503,
            title = "Ir a la universidad",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(10, 30),
            endTime = LocalTime.of(11, 0)
        ),
        ScheduleTask(
            id = 504,
            title = "Clase: Dev Ops (R2.10)",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(13, 0)
        ),
        ScheduleTask(
            id = 505,
            title = "Hora de comer",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(14, 0)
        ),
        ScheduleTask(
            id = 506,
            title = "Descanso de la semana",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(18, 0)
        ),
        ScheduleTask(
            id = 507,
            title = "Proyectos personales",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(19, 30)
        ),
        ScheduleTask(
            id = 508,
            title = "Objetivo físico",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(19, 30),
            endTime = LocalTime.of(20, 30)
        ),
        ScheduleTask(
            id = 509,
            title = "Hora de cenar",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(20, 30),
            endTime = LocalTime.of(21, 30)
        ),
        ScheduleTask(
            id = 510,
            title = "Tiempo con Irene/amigos",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(21, 30),
            endTime = LocalTime.of(23, 0)
        )

        // Se han eliminado las tareas de SÁBADO y DOMINGO según lo solicitado
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