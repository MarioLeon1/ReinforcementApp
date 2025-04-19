package com.example.reinforcement.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reinforcement.databinding.FragmentScheduleBinding
import com.example.reinforcement.ui.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import java.time.DayOfWeek
import java.time.LocalTime

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Utilizar la fábrica de ViewModels personalizada
        val factory = ViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ScheduleViewModel::class.java]
        
        setupTabLayout()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupTabLayout() {
        binding.tabLayoutDays.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.tag?.let {
                    if (it is DayOfWeek) {
                        viewModel.setSelectedDay(it)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = ScheduleAdapter { taskId ->
            viewModel.toggleTaskCompletion(taskId)
        }
        
        binding.recyclerSchedule.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ScheduleFragment.adapter
        }
    }

    private fun observeViewModel() {
        // Observar cambios en las etiquetas de fecha
        viewModel.dateLabels.observe(viewLifecycleOwner) { labels ->
            binding.tabLayoutDays.removeAllTabs()
            
            // Añadir una pestaña para cada día de la semana
            for (dayOfWeek in DayOfWeek.values()) {
                val tab = binding.tabLayoutDays.newTab()
                    .setText(getDayName(dayOfWeek))
                    .setTag(dayOfWeek)
                
                binding.tabLayoutDays.addTab(tab)
                
                // Seleccionar la pestaña correspondiente al día actual
                if (dayOfWeek == viewModel.selectedDay.value) {
                    tab.select()
                }
            }
        }
        
        // Observar cambios en el día seleccionado y las tareas
        viewModel.scheduleTasks.observe(viewLifecycleOwner) { tasks ->
            val scheduleItems = generateScheduleItems(tasks)
            adapter.submitList(scheduleItems, viewModel.currentTimeIndicator.value)
        }
        
        // Observar cambios en el indicador de tiempo actual
        viewModel.currentTimeIndicator.observe(viewLifecycleOwner) { currentTime ->
            // Regenerar los items con el nuevo tiempo actual
            viewModel.scheduleTasks.value?.let { tasks ->
                val scheduleItems = generateScheduleItems(tasks)
                adapter.submitList(scheduleItems, currentTime)
            }
        }
    }
    
    private fun generateScheduleItems(tasks: List<com.example.reinforcement.data.model.ScheduleTask>): List<ScheduleItem> {
        val items = mutableListOf<ScheduleItem>()
        
        // Generar horas vacías y tareas desde las 8:00 hasta las 00:00
        var currentHour = LocalTime.of(8, 0)
        val endHour = LocalTime.of(0, 0)
        
        while (!currentHour.equals(endHour)) {
            // Buscar tareas que empiecen en esta hora
            val tasksAtHour = tasks.filter { 
                it.startTime.hour == currentHour.hour && it.startTime.minute == currentHour.minute 
            }
            
            if (tasksAtHour.isEmpty()) {
                // Si no hay tareas, agregar una hora vacía
                items.add(ScheduleItem.EmptyHour(currentHour))
            } else {
                // Si hay tareas, agregar cada tarea
                tasksAtHour.forEach { task ->
                    items.add(
                        ScheduleItem.TaskItem(
                            id = task.id,
                            title = task.title,
                            startTime = task.startTime,
                            endTime = task.endTime,
                            isCompleted = task.isCompleted
                        )
                    )
                }
            }
            
            // Avanzar a la siguiente hora
            currentHour = currentHour.plusHours(1)
        }
        
        return items
    }
    
    private fun getDayName(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Lunes"
            DayOfWeek.TUESDAY -> "Martes"
            DayOfWeek.WEDNESDAY -> "Miércoles"
            DayOfWeek.THURSDAY -> "Jueves"
            DayOfWeek.FRIDAY -> "Viernes"
            DayOfWeek.SATURDAY -> "Sábado"
            DayOfWeek.SUNDAY -> "Domingo"
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la hora actual y comenzar actualizaciones periódicas
        viewModel.updateCurrentTime()
        viewModel.startTimeUpdates()
    }

    override fun onPause() {
        super.onPause()
        // Detener actualizaciones periódicas cuando el fragmento no está visible
        viewModel.stopTimeUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}