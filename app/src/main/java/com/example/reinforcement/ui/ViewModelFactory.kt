package com.example.reinforcement.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reinforcement.ReinforcementApplication
import com.example.reinforcement.ui.cigarettes.CigarettesViewModel
import com.example.reinforcement.ui.goals.DailyGoalsViewModel
import com.example.reinforcement.ui.points.PointsViewModel
import com.example.reinforcement.ui.schedule.ScheduleViewModel
import com.example.reinforcement.ui.todo.TodoViewModel

/**
 * Fábrica de ViewModels personalizada que proporciona las dependencias necesarias
 * para cada tipo de ViewModel, utilizando los repositorios de la aplicación.
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val app = application as ReinforcementApplication
        
        return when {
            modelClass.isAssignableFrom(CigarettesViewModel::class.java) -> {
                CigarettesViewModel(application) as T
            }
            modelClass.isAssignableFrom(DailyGoalsViewModel::class.java) -> {
                DailyGoalsViewModel(application) as T
            }
            modelClass.isAssignableFrom(ScheduleViewModel::class.java) -> {
                ScheduleViewModel(application) as T
            }
            modelClass.isAssignableFrom(TodoViewModel::class.java) -> {
                TodoViewModel(application) as T
            }
            modelClass.isAssignableFrom(PointsViewModel::class.java) -> {
                PointsViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}