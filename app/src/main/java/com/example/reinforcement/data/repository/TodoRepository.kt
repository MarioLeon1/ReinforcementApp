package com.example.reinforcement.data.repository

import android.content.Context
import com.example.reinforcement.data.model.TodoTask
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger

class TodoRepository(context: Context) {
    private val preferenceManager = PreferenceManager(context)
    
    // Generador de IDs únicos
    private val idCounter = AtomicInteger(1000)
    
    // Lista mutable para almacenar todas las tareas
    private val allTasks = mutableListOf<TodoTask>()
    
    init {
        // Cargar tareas guardadas desde SharedPreferences
        loadSavedTasks()
        
        // Agregar tareas por defecto solo si no hay tareas guardadas
        if (allTasks.isEmpty()) {
            addDefaultTasks()
        }
    }
    
    private fun loadSavedTasks() {
        val taskIds = preferenceManager.getTodoTasksIds()
        
        for (idStr in taskIds) {
            val id = idStr.toIntOrNull() ?: continue
            
            val title = preferenceManager.getTodoTaskTitle(id) ?: continue
            val date = preferenceManager.getTodoTaskDate(id) ?: LocalDate.now()
            val isCompleted = preferenceManager.getTodoTaskCompletionState(id)
            
            allTasks.add(
                TodoTask(
                    id = id,
                    title = title,
                    date = date,
                    isCompleted = isCompleted
                )
            )
            
            // Actualizar el contador de IDs si es necesario
            if (id >= idCounter.get()) {
                idCounter.set(id + 1)
            }
        }
    }
    
    private fun addDefaultTasks() {
        val defaultTasks = listOf(
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
        
        allTasks.addAll(defaultTasks)
        
        // Guardar las tareas por defecto en SharedPreferences
        for (task in defaultTasks) {
            saveTaskToPreferences(task)
        }
    }
    
    private fun saveTaskToPreferences(task: TodoTask) {
        preferenceManager.saveTodoTask(
            taskId = task.id,
            title = task.title,
            date = task.date,
            isCompleted = task.isCompleted
        )
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
        
        // Guardar en SharedPreferences
        saveTaskToPreferences(newTask)
        
        return newTask
    }
    
    // Cambiar el estado de completado de una tarea
    fun toggleTaskCompletion(taskId: Int): Boolean {
        val taskIndex = allTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = allTasks[taskIndex]
            task.isCompleted = !task.isCompleted
            allTasks[taskIndex] = task
            
            // Actualizar en SharedPreferences
            saveTaskToPreferences(task)
            
            return task.isCompleted
        }
        return false
    }
    
    // Eliminar una tarea
    fun deleteTask(taskId: Int): Boolean {
        val taskIndex = allTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            allTasks.removeAt(taskIndex)
            
            // Eliminar de SharedPreferences
            preferenceManager.deleteTodoTask(taskId)
            
            return true
        }
        return false
    }
    
    // Actualizar la fecha de las tareas de hoy a ayer cuando ocurre un cambio de día
    fun preserveTasksCompletionState(yesterday: LocalDate) {
        val today = LocalDate.now()
        
        // Obtener todas las tareas de hoy
        val todayTasks = allTasks.filter { it.date.isEqual(today) }
        
        // Para cada tarea de hoy, crear una copia para ayer y mantener su estado
        for (task in todayTasks) {
            // Si la tarea está completada, crear una copia para ayer con el mismo estado
            if (task.isCompleted) {
                // Crear una nueva tarea para ayer con el mismo título y estado
                val yesterdayTask = TodoTask(
                    id = idCounter.getAndIncrement(),
                    title = task.title,
                    date = yesterday,
                    isCompleted = true,
                    creationDate = task.creationDate
                )
                
                // Guardar la tarea de ayer
                allTasks.add(yesterdayTask)
                saveTaskToPreferences(yesterdayTask)
                
                // Resetear el estado de la tarea de hoy
                task.isCompleted = false
                saveTaskToPreferences(task)
            }
        }
    }
}