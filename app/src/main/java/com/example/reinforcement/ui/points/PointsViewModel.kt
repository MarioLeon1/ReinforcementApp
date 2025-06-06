package com.example.reinforcement.ui.points

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reinforcement.data.model.PointCategory
import com.example.reinforcement.data.model.PointEntry
import com.example.reinforcement.data.model.PointsData
import com.example.reinforcement.data.repository.CigarettesRepository
import com.example.reinforcement.data.repository.DailyGoalsRepository
import com.example.reinforcement.data.repository.MotivationalPhraseRepository
import com.example.reinforcement.data.repository.PointsRepository
import com.example.reinforcement.data.repository.ScheduleRepository
import com.example.reinforcement.data.repository.TodoRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class PointsViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorios
    private val dailyGoalsRepository = DailyGoalsRepository(application)
    private val scheduleRepository = ScheduleRepository(application)
    private val todoRepository = TodoRepository(application)
    private val cigarettesRepository = CigarettesRepository(application)
    private val phraseRepository = MotivationalPhraseRepository(application)

    private val pointsRepository = PointsRepository(
        dailyGoalsRepository,
        scheduleRepository,
        todoRepository,
        cigarettesRepository
    )

    // LiveData
    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _pointsData = MutableLiveData<PointsData>()
    val pointsData: LiveData<PointsData> = _pointsData

    private val _dateLabels = MutableLiveData<List<Pair<LocalDate, String>>>()
    val dateLabels: LiveData<List<Pair<LocalDate, String>>> = _dateLabels

    private val _motivationalPhrase = MutableLiveData<String>()
    val motivationalPhrase: LiveData<String> = _motivationalPhrase

    private val _progressPercentage = MutableLiveData<Float>()
    val progressPercentage: LiveData<Float> = _progressPercentage

    private val _goalReached = MutableLiveData<Boolean>()
    val goalReached: LiveData<Boolean> = _goalReached

    init {
        // Iniciar con la fecha actual
        _selectedDate.value = LocalDate.now()

        // Cargar datos de puntos para la fecha seleccionada
        loadPointsForSelectedDate()

        // Generar etiquetas para las fechas (Ayer, Hoy, Mañana)
        generateDateLabels()

        // Cargar frase motivacional
        _motivationalPhrase.value = phraseRepository.getDailyPhrase()
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadPointsForSelectedDate()
    }

    private fun loadPointsForSelectedDate() {
        val date = selectedDate.value ?: LocalDate.now()
        val pointsData = pointsRepository.calculatePointsForDate(date)
        _pointsData.value = pointsData

        // Calcular el porcentaje de progreso para el gráfico (0-100%)
        val totalPoints = pointsData.totalPoints
        val progressPercentage = when {
            totalPoints <= 0 -> 0f
            totalPoints >= 100 -> 1f
            else -> totalPoints / 100f
        }
        _progressPercentage.value = progressPercentage

        // Determinar si se ha alcanzado el objetivo de 100 puntos
        _goalReached.value = totalPoints >= 100
    }

    private fun generateDateLabels() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))

        val labels = listOf(
            Pair(today.minusDays(1), "Ayer\n${today.minusDays(1).format(formatter)}"),
            Pair(today, "Hoy\n${today.format(formatter)}"),
            Pair(today.plusDays(1), "Mañana\n${today.plusDays(1).format(formatter)}")
        )

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

    // Obtener puntos agrupados por categoría
    fun getPointsByCategory(): Map<PointCategory, List<PointEntry>> {
        return pointsData.value?.pointsBreakdown?.groupBy { it.category } ?: emptyMap()
    }

    // Método para forzar recálculo de puntos
    fun calculatePoints() {
        loadPointsForSelectedDate()
    }
}