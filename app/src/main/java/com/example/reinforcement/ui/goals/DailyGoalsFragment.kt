package com.example.reinforcement.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reinforcement.databinding.FragmentDailyGoalsBinding

class DailyGoalsFragment : Fragment() {

    private var _binding: FragmentDailyGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DailyGoalsViewModel
    private lateinit var physicalGoalsAdapter: DailyGoalAdapter
    private lateinit var mentalGoalsAdapter: DailyGoalAdapter
    private lateinit var disciplineGoalsAdapter: DailyGoalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[DailyGoalsViewModel::class.java]
        
        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        // Configurar adapter para objetivos físicos
        physicalGoalsAdapter = DailyGoalAdapter { goal ->
            viewModel.toggleGoalCompletion(goal.id)
        }
        binding.recyclerViewPhysicalGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = physicalGoalsAdapter
        }
        
        // Configurar adapter para objetivos mentales
        mentalGoalsAdapter = DailyGoalAdapter { goal ->
            viewModel.toggleGoalCompletion(goal.id)
        }
        binding.recyclerViewMentalGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mentalGoalsAdapter
        }
        
        // Configurar adapter para objetivos de disciplina
        disciplineGoalsAdapter = DailyGoalAdapter { goal ->
            viewModel.toggleGoalCompletion(goal.id)
        }
        binding.recyclerViewDisciplineGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = disciplineGoalsAdapter
        }
    }

    private fun observeViewModel() {
        // Observar frase motivacional
        viewModel.motivationalPhrase.observe(viewLifecycleOwner) { phrase ->
            binding.textMotivationalPhrase.text = phrase
        }
        
        // Observar objetivos físicos
        viewModel.physicalGoals.observe(viewLifecycleOwner) { goals ->
            physicalGoalsAdapter.submitList(goals)
        }
        
        // Observar objetivos mentales
        viewModel.mentalGoals.observe(viewLifecycleOwner) { goals ->
            mentalGoalsAdapter.submitList(goals)
        }
        
        // Observar objetivos de disciplina
        viewModel.disciplineGoals.observe(viewLifecycleOwner) { goals ->
            disciplineGoalsAdapter.submitList(goals)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}