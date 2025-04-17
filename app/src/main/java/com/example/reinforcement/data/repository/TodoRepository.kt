package com.example.reinforcement.data.repository

import com.example.reinforcement.data.model.TodoTask
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger

class TodoRepository {
    // Generador de IDs únicos
    private val idCounter = AtomicInteger(1000)
    
    // Ejemplo de tareas iniciales (en una app real, estas vendrían de una base de datos)
    private val defaultTasks = mutableListOf(
        TodoTask(
            id = idCounter.getAndIncrement(),
            title = "Hacer el trabajo de Redes y Sistemas",
            date = LocalDate.now(),
            isCompleted = false
        ),
        TodoTask(
            id = idCounter.getAndIncrement(),
            title = "Tomar apuntes de Ciberseguridad",
            date = LocalDate.now(),
            isCompleted = false
        ),
        TodoTask(
            id = idCounter.getAndIncrement(),
            title = "Ir al Mercadona a por caramelos",
            date = LocalDate.now(),
            isCompleted = false
        ),
        TodoTask(
            id = idCounter.getAndIncrement(),
            title = "Sacar cajas de la terraza",
            date = LocalDate.now(),
            isCompleted = false
        ),
        // Tareas para otros días
        TodoTask(
            id = idCounter.getAndIncrement(),
            title = "Entregar proyecto final",
            date = LocalDate.now().plusDays(2),
            isCompleted = false
        ),
        TodoTask(
            id = idCounter.getAndIncrement(),
            title = "Preparar presentación",
            date = LocalDate.now().minusDays(1),
            isCompleted = true
        )
    )
    
    // Lista mutable para almacenar todas las tareas
    private val allTasks = mutableListOf<TodoTask>().apply {
        addAll(defaultTasks)
    }
    
    // Obtener tareas para una fecha específica
    fun getTasksForDate(date: LocalDate): List<TodoTask> {
        return allTasks.filter { it.date.isEqual(date) }
    }
    
    // Añadir una nueva tarea
    fun addTask(title: String, date: LocalDate): TodoTask {
        val newTask = TodoTask(
            id = idCounter.getAndIncrement(),
            title = title,
            date = date,
            isCompleted = false
        )
        allTasks.add(newTask)
        return newTask
    }
    
    // Cambiar el estado de completado de una tarea
    fun toggleTaskCompletion(taskId: Int): Boolean {
        val taskIndex = allTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = allTasks[taskIndex]
            task.isCompleted = !task.isCompleted
            allTasks[taskIndex] = task
            return task.isCompleted
        }
        return false
    }
    
    // Eliminar una tarea
    fun deleteTask(taskId: Int): Boolean {
        val taskIndex = allTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            allTasks.removeAt(taskIndex)
            return true
        }
        return false
    }
}