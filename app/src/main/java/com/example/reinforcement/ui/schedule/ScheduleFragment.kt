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
        val result = mutableListOf<ScheduleItem>()
        val sortedTasks = tasks.sortedBy { it.startTime }

        // Generamos solo horas vacías donde no hay tareas
        // Primero, creamos un conjunto para marcar las franjas horarias cubiertas por tareas
        val coveredTimeSlots = mutableSetOf<Int>()

        // Encontrar la última hora para la que necesitamos mostrar datos
        // (esto evita mostrar horas vacías después de la última tarea)
        var latestEndTimeMinutes = 0
        for (task in sortedTasks) {
            val endMinutes = task.endTime.hour * 60 + task.endTime.minute
            if (endMinutes > latestEndTimeMinutes) {
                latestEndTimeMinutes = endMinutes
            }
        }

        // Registramos todas las horas cubiertas por tareas (en minutos desde las 00:00)
        for (task in sortedTasks) {
            // Convertir startTime y endTime a minutos desde las 00:00
            val startMinutes = task.startTime.hour * 60 + task.startTime.minute
            val endMinutes = task.endTime.hour * 60 + task.endTime.minute

            // Marcar todos los slots de 30 minutos cubiertos por esta tarea
            var currentSlot = (startMinutes / 30) * 30 // Redondear al slot de 30 minutos
            while (currentSlot < endMinutes) {
                coveredTimeSlots.add(currentSlot)
                currentSlot += 30
            }
        }

        // Añadir horas vacías para las horas no cubiertas (entre 8:00 y la última hora)
        // Calculamos el último slot que necesitamos mostrar
        val lastSlotToShow = ((latestEndTimeMinutes + 29) / 30) * 30

        // Comenzar desde las 8:00 (480 minutos)
        var currentSlot = 8 * 60
        while (currentSlot <= lastSlotToShow) {
            if (currentSlot !in coveredTimeSlots) {
                val hour = currentSlot / 60
                val minute = currentSlot % 60
                result.add(ScheduleItem.EmptyHour(LocalTime.of(hour, minute)))
            }
            currentSlot += 30
        }

        // Añadir todas las tareas
        for (task in sortedTasks) {
            result.add(
                ScheduleItem.TaskItem(
                    id = task.id,
                    title = task.title,
                    startTime = task.startTime,
                    endTime = task.endTime,
                    isCompleted = task.isCompleted
                )
            )
        }

        // Ordenar todo por hora
        return result.sortedWith(compareBy {
            when (it) {
                is ScheduleItem.EmptyHour -> it.time
                is ScheduleItem.TaskItem -> it.startTime
            }
        })
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