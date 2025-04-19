package com.example.reinforcement.ui.points

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reinforcement.R
import com.example.reinforcement.data.model.PointCategory
import com.example.reinforcement.databinding.FragmentPointsBinding
import com.example.reinforcement.ui.ViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class PointsFragment : Fragment() {

    private var _binding: FragmentPointsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PointsViewModel
    
    // Adaptadores para cada categoría
    private val dailyGoalsAdapter = PointEntryAdapter()
    private val scheduleAdapter = PointEntryAdapter()
    private val todoAdapter = PointEntryAdapter()
    private val cigarettesAdapter = PointEntryAdapter()
    private val bonusAdapter = PointEntryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Utilizar el ViewModelFactory personalizado
        val factory = ViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[PointsViewModel::class.java]
        
        setupRecyclerViews()
        setupDateNavigation()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        // Configurar RecyclerView para objetivos diarios
        binding.recyclerDailyGoalsPoints.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dailyGoalsAdapter
        }
        
        // Configurar RecyclerView para horario
        binding.recyclerSchedulePoints.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
        }
        
        // Configurar RecyclerView para To-Do
        binding.recyclerTodoPoints.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
        
        // Configurar RecyclerView para cigarros
        binding.recyclerCigarettesPoints.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cigarettesAdapter
        }
        
        // Configurar RecyclerView para bonus
        binding.recyclerBonusPoints.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bonusAdapter
        }
    }

    private fun setupDateNavigation() {
        binding.textPreviousDate.setOnClickListener {
            viewModel.goToPreviousDate()
        }
        
        binding.textNextDate.setOnClickListener {
            viewModel.goToNextDate()
        }
    }

    private fun observeViewModel() {
        // Observar cambios en los datos de puntos
        viewModel.pointsData.observe(viewLifecycleOwner) { pointsData ->
            // Actualizar total de puntos
            binding.textTotalPoints.text = getString(R.string.points_total, pointsData.totalPoints)
            
            // Obtener puntos por categoría
            val pointsByCategory = viewModel.getPointsByCategory()
            
            // Actualizar adaptadores para cada categoría
            dailyGoalsAdapter.submitList(pointsByCategory[PointCategory.DAILY_GOAL] ?: emptyList())
            scheduleAdapter.submitList(pointsByCategory[PointCategory.SCHEDULE] ?: emptyList())
            todoAdapter.submitList(pointsByCategory[PointCategory.TODO] ?: emptyList())
            cigarettesAdapter.submitList(pointsByCategory[PointCategory.CIGARETTES] ?: emptyList())
            bonusAdapter.submitList(pointsByCategory[PointCategory.BONUS] ?: emptyList())
        }
        
        // Observar cambios en la fecha seleccionada
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            updateDateDisplay(date)
        }
    }

    private fun updateDateDisplay(selectedDate: LocalDate) {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
        
        // Configurar fecha actual
        val currentDateText = when {
            selectedDate.isEqual(today) -> "Hoy\n${selectedDate.format(formatter)}"
            selectedDate.isEqual(today.minusDays(1)) -> "Ayer\n${selectedDate.format(formatter)}"
            selectedDate.isEqual(today.plusDays(1)) -> "Mañana\n${selectedDate.format(formatter)}"
            else -> selectedDate.format(formatter)
        }
        binding.textCurrentDate.text = currentDateText
        
        // Configurar día anterior
        val previousDate = selectedDate.minusDays(1)
        val previousDateText = when {
            previousDate.isEqual(today) -> "Hoy\n${previousDate.format(formatter)}"
            previousDate.isEqual(today.minusDays(1)) -> "Ayer\n${previousDate.format(formatter)}"
            previousDate.isEqual(today.plusDays(1)) -> "Mañana\n${previousDate.format(formatter)}"
            else -> previousDate.format(formatter)
        }
        binding.textPreviousDate.text = previousDateText
        
        // Configurar día siguiente
        val nextDate = selectedDate.plusDays(1)
        val nextDateText = when {
            nextDate.isEqual(today) -> "Hoy\n${nextDate.format(formatter)}"
            nextDate.isEqual(today.minusDays(1)) -> "Ayer\n${nextDate.format(formatter)}"
            nextDate.isEqual(today.plusDays(1)) -> "Mañana\n${nextDate.format(formatter)}"
            else -> nextDate.format(formatter)
        }
        binding.textNextDate.text = nextDateText
    }

    // Método para forzar la actualización de datos
    fun refreshData() {
        // Forzar el cálculo de los puntos nuevamente
        viewModel.calculatePoints()
    }

    override fun onResume() {
        super.onResume()
        // También actualizar datos cuando el fragmento se reanuda
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}