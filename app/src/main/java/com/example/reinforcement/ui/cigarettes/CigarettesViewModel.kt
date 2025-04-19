package com.example.reinforcement.ui.cigarettes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reinforcement.data.model.CigaretteData
import com.example.reinforcement.data.repository.CigarettesRepository
import com.example.reinforcement.data.repository.MotivationalPhraseRepository
import java.time.LocalDate

class CigarettesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val cigarettesRepository = CigarettesRepository(application)
    private val phraseRepository = MotivationalPhraseRepository(application)
    
    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> = _selectedDate
    
    private val _cigaretteData = MutableLiveData<CigaretteData>()
    val cigaretteData: LiveData<CigaretteData> = _cigaretteData
    
    private val _progressPercentage = MutableLiveData<Float>()
    val progressPercentage: LiveData<Float> = _progressPercentage
    
    private val _remainingMessage = MutableLiveData<String>()
    val remainingMessage: LiveData<String> = _remainingMessage
    
    private val _motivationalPhrase = MutableLiveData<String>()
    val motivationalPhrase: LiveData<String> = _motivationalPhrase
    
    init {
        // Iniciar con la fecha actual
        _selectedDate.value = LocalDate.now()
        
        // Cargar datos de cigarros para la fecha seleccionada
        loadCigaretteData()
        
        // Cargar frase motivacional
        _motivationalPhrase.value = phraseRepository.getDailyPhrase()
    }
    
    fun incrementCigaretteCount() {
        val date = selectedDate.value ?: LocalDate.now()
        cigarettesRepository.incrementCigaretteCount(date)
        loadCigaretteData()
    }
    
    private fun loadCigaretteData() {
        val date = selectedDate.value ?: LocalDate.now()
        val data = cigarettesRepository.getCigaretteData(date)
        _cigaretteData.value = data
        
        // Calcular el porcentaje de progreso para el gráfico
        val percentage = minOf(data.count.toFloat() / data.limit.toFloat(), 1f)
        _progressPercentage.value = percentage
        
        // Generar mensaje de cigarros restantes
        _remainingMessage.value = if (data.count < data.limit) {
            "Puedes fumar ${data.limit - data.count} más"
        } else {
            "Intenta no fumar más"
        }
    }
    
    fun resetCigaretteCount() {
        val date = selectedDate.value ?: LocalDate.now()
        cigarettesRepository.resetCigaretteCount(date)
        loadCigaretteData()
    }
}