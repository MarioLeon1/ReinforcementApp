package com.example.reinforcement.ui.todo

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reinforcement.R
import com.example.reinforcement.databinding.DialogAddTodoBinding
import com.example.reinforcement.databinding.FragmentTodoBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TodoFragment : Fragment() {

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TodoViewModel
    private lateinit var adapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        
        setupRecyclerView()
        setupDateNavigation()
        setupAddTaskButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TodoAdapter { task ->
            viewModel.toggleTaskCompletion(task.id)
        }
        
        binding.recyclerTodo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TodoFragment.adapter
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

    private fun setupAddTaskButton() {
        binding.buttonAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTodoBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_task)
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.buttonSave.setOnClickListener {
            val taskTitle = dialogBinding.editTextTaskTitle.text.toString().trim()
            if (taskTitle.isNotEmpty()) {
                viewModel.addTask(taskTitle)
                dialog.dismiss()
            }
        }
        
        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun observeViewModel() {
        // Observar la frase motivacional
        viewModel.motivationalPhrase.observe(viewLifecycleOwner) { phrase ->
            binding.textMotivationalPhrase.text = phrase
        }
        
        // Observar cambios en la fecha seleccionada
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            updateDateDisplay(date)
        }
        
        // Observar cambios en las tareas
        viewModel.todoTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}