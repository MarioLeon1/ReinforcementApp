package com.example.reinforcement.ui.goals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reinforcement.data.model.DailyGoal
import com.example.reinforcement.data.model.GoalCategory
import com.example.reinforcement.data.repository.DailyGoalsRepository
import com.example.reinforcement.data.repository.MotivationalPhraseRepository

class DailyGoalsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val goalsRepository = DailyGoalsRepository()
    private val phraseRepository = MotivationalPhraseRepository(application)
    
    private val _physicalGoals = MutableLiveData<List<DailyGoal>>()
    val physicalGoals: LiveData<List<DailyGoal>> = _physicalGoals
    
    private val _mentalGoals = MutableLiveData<List<DailyGoal>>()
    val mentalGoals: LiveData<List<DailyGoal>> = _mentalGoals
    
    private val _disciplineGoals = MutableLiveData<List<DailyGoal>>()
    val disciplineGoals: LiveData<List<DailyGoal>> = _disciplineGoals
    
    private val _motivationalPhrase = MutableLiveData<String>()
    val motivationalPhrase: LiveData<String> = _motivationalPhrase
    
    init {
        loadGoals()
        updateMotivationalPhrase()
    }
    
    fun loadGoals() {
        _physicalGoals.value = goalsRepository.getGoalsByCategory(GoalCategory.PHYSICAL)
        _mentalGoals.value = goalsRepository.getGoalsByCategory(GoalCategory.MENTAL)
        _disciplineGoals.value = goalsRepository.getGoalsByCategory(GoalCategory.DISCIPLINE)
    }
    
    fun toggleGoalCompletion(goalId: Int) {
        goalsRepository.toggleGoalCompletion(goalId)
        loadGoals()
    }
    
    fun updateMotivationalPhrase() {
        _motivationalPhrase.value = phraseRepository.getDailyPhrase()
    }
    
    fun getCompletedGoalsCount(): Int {
        return goalsRepository.getCompletedGoalsCount()
    }
}