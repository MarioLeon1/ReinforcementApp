package com.example.reinforcement.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reinforcement.data.model.ScheduleTask
import com.example.reinforcement.data.repository.MotivationalPhraseRepository
import com.example.reinforcement.data.repository.ScheduleRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import android.os.Handler
import android.os.Looper
import java.util.Timer
import java.util.TimerTask

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    
    private val scheduleRepository = ScheduleRepository(application)
    private val phraseRepository = MotivationalPhraseRepository(application)
    
    private val _selectedDay = MutableLiveData<DayOfWeek>()
    val selectedDay: LiveData<DayOfWeek> = _selectedDay
    
    private val _scheduleTasks = MutableLiveData<List<ScheduleTask>>()
    val scheduleTasks: LiveData<List<ScheduleTask>> = _scheduleTasks
    
    private val _currentTimeIndicator = MutableLiveData<LocalTime>()
    val currentTimeIndicator: LiveData<LocalTime> = _currentTimeIndicator
    
    private val _dateLabels = MutableLiveData<List<Pair<DayOfWeek, String>>>()
    val dateLabels: LiveData<List<Pair<DayOfWeek, String>>> = _dateLabels
    
    private val _motivationalPhrase = MutableLiveData<String>()
    val motivationalPhrase: LiveData<String> = _motivationalPhrase

    private val handler = Handler(Looper.getMainLooper())
    private val timeUpdateRunnable = object : Runnable {
        override fun run() {
            updateCurrentTime()
            // Actualizar cada minuto
            handler.postDelayed(this, 60000)
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(timeUpdateRunnable)
    }

    fun startTimeUpdates() {
        handler.post(timeUpdateRunnable)
    }

    fun stopTimeUpdates() {
        handler.removeCallbacks(timeUpdateRunnable)
    }
    
    init {
        // Iniciar con el día actual de la semana
        _selectedDay.value = LocalDate.now().dayOfWeek
        
        // Actualizar la lista de tareas para el día seleccionado
        loadTasksForSelectedDay()
        
        // Actualizar la hora actual
        updateCurrentTime()
        
        // Generar etiquetas para los días (Ayer, Hoy, Mañana)
        generateDateLabels()
        
        // Cargar frase motivacional
        _motivationalPhrase.value = phraseRepository.getDailyPhrase()
    }
    
    fun setSelectedDay(dayOfWeek: DayOfWeek) {
        _selectedDay.value = dayOfWeek
        loadTasksForSelectedDay()
    }
    
    fun toggleTaskCompletion(taskId: Int) {
        val day = selectedDay.value ?: LocalDate.now().dayOfWeek
        val selectedDate = LocalDate.now().with(day)
        
        // Solo permitir cambiar tareas que estén antes de la hora actual
        val task = scheduleRepository.getTaskById(taskId)
        val currentTime = LocalTime.now()
        
        if (task != null && (selectedDate.isBefore(LocalDate.now()) || 
                            (selectedDate.isEqual(LocalDate.now()) && task.endTime.isBefore(currentTime)))) {
            scheduleRepository.toggleTaskCompletion(taskId, selectedDate)
            loadTasksForSelectedDay()
        }
    }
    
    fun updateCurrentTime() {
        _currentTimeIndicator.value = LocalTime.now()
    }
    
    private fun loadTasksForSelectedDay() {
        val day = selectedDay.value ?: LocalDate.now().dayOfWeek
        _scheduleTasks.value = scheduleRepository.getTasksForDay(day)
    }
    
    private fun generateDateLabels() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
        
        val labels = listOf(
            Pair(today.minusDays(1).dayOfWeek, "Ayer\n${today.minusDays(1).format(formatter)}"),
            Pair(today.dayOfWeek, "Hoy\n${today.format(formatter)}"),
            Pair(today.plusDays(1).dayOfWeek, "Mañana\n${today.plusDays(1).format(formatter)}")
        )
        
        _dateLabels.value = labels
    }
    
    // Método para obtener el nombre del día seleccionado
    fun getSelectedDayName(): String {
        val day = selectedDay.value ?: LocalDate.now().dayOfWeek
        return day.getDisplayName(TextStyle.FULL, Locale("es", "ES")).capitalize()
    }
}