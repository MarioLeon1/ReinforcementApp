package com.example.reinforcement.ui.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reinforcement.data.model.TodoTask
import com.example.reinforcement.data.repository.MotivationalPhraseRepository
import com.example.reinforcement.data.repository.TodoRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    
    private val todoRepository = TodoRepository()
    private val phraseRepository = MotivationalPhraseRepository(application)
    
    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate
    
    private val _todoTasks = MutableLiveData<List<TodoTask>>()
    val todoTasks: LiveData<List<TodoTask>> = _todoTasks
    
    private val _dateLabels = MutableLiveData<List<Pair<LocalDate, String>>>()
    val dateLabels: LiveData<List<Pair<LocalDate, String>>> = _dateLabels
    
    private val _motivationalPhrase = MutableLiveData<String>()
    val motivationalPhrase: LiveData<String> = _motivationalPhrase
    
    init {
        // Iniciar con la fecha actual
        _selectedDate.value = LocalDate.now()
        
        // Cargar tareas para la fecha seleccionada
        loadTasksForSelectedDate()
        
        // Generar etiquetas para las fechas (Ayer, Hoy, Mañana)
        generateDateLabels()
        
        // Cargar frase motivacional
        _motivationalPhrase.value = phraseRepository.getDailyPhrase()
    }
    
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadTasksForSelectedDate()
    }
    
    fun addTask(title: String) {
        val date = selectedDate.value ?: LocalDate.now()
        todoRepository.addTask(title, date)
        loadTasksForSelectedDate()
    }
    
    fun toggleTaskCompletion(taskId: Int) {
        todoRepository.toggleTaskCompletion(taskId)
        loadTasksForSelectedDate()
    }
    
    fun deleteTask(taskId: Int) {
        todoRepository.deleteTask(taskId)
        loadTasksForSelectedDate()
    }
    
    private fun loadTasksForSelectedDate() {
        val date = selectedDate.value ?: LocalDate.now()
        _todoTasks.value = todoRepository.getTasksForDate(date)
    }
    
    private fun generateDateLabels() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM")
        
        val daysBefore = 3
        val daysAfter = 3
        val labels = mutableListOf<Pair<LocalDate, String>>()
        
        for (i in -daysBefore..daysAfter) {
            val date = today.plusDays(i.toLong())
            val label = when (i) {
                -1 -> "Ayer\n${date.format(formatter)}"
                0 -> "Hoy\n${date.format(formatter)}"
                1 -> "Mañana\n${date.format(formatter)}"
                else -> "${date.format(formatter)}"
            }
            labels.add(Pair(date, label))
        }
        
        _dateLabels.value = labels
    }
    
    fun goToPreviousDate() {
        val currentDate = selectedDate.value ?: LocalDate.now()
        setSelectedDate(currentDate.minusDays(1))
    }
    
    fun goToNextDate() {
        val currentDate = selectedDate.value ?: LocalDate.now()
        setSelectedDate(currentDate.plusDays(1))
    }
}