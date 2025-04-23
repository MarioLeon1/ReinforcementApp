package com.example.reinforcement.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.reinforcement.R
import com.example.reinforcement.databinding.ItemScheduleEmptyHourBinding
import com.example.reinforcement.databinding.ItemScheduleTaskBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.min

class ScheduleAdapter(
    private val onTaskClicked: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ScheduleItem>()
    private var currentTime: LocalTime? = null
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    companion object {
        private const val VIEW_TYPE_HOUR = 0
        private const val VIEW_TYPE_TASK = 1
    }

    fun submitList(newItems: List<ScheduleItem>, currentTime: LocalTime?) {
        items.clear()
        items.addAll(newItems)
        this.currentTime = currentTime
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ScheduleItem.EmptyHour -> VIEW_TYPE_HOUR
            is ScheduleItem.TaskItem -> VIEW_TYPE_TASK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HOUR -> {
                val binding = ItemScheduleEmptyHourBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                EmptyHourViewHolder(binding)
            }
            VIEW_TYPE_TASK -> {
                val binding = ItemScheduleTaskBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TaskViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ScheduleItem.EmptyHour -> (holder as EmptyHourViewHolder).bind(item, position)
            is ScheduleItem.TaskItem -> (holder as TaskViewHolder).bind(item, position)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun shouldShowTimeIndicator(position: Int): Boolean {
        if (currentTime == null) return false

        // Obtener el elemento actual
        val currentItem = items[position]

        when (currentItem) {
            is ScheduleItem.EmptyHour -> {
                // Para las horas vacías, mostrar el indicador si cae dentro de esta franja de media hora
                val slotStartMinutes = currentItem.time.hour * 60 + currentItem.time.minute
                val slotEndMinutes = slotStartMinutes + 30
                val currentMinutes = currentTime!!.hour * 60 + currentTime!!.minute

                return currentMinutes >= slotStartMinutes &&
                        currentMinutes < slotEndMinutes &&
                        !isTimeWithinAnyTask(currentTime!!)
            }
            is ScheduleItem.TaskItem -> {
                // Para las tareas, mostrar el indicador si la hora está dentro de la tarea
                return currentTime!!.isAfter(currentItem.startTime.minusMinutes(1)) &&
                        currentTime!!.isBefore(currentItem.endTime)
            }
        }
    }

    // Verifica si el tiempo actual está dentro de alguna tarea
    private fun isTimeWithinAnyTask(time: LocalTime): Boolean {
        return items.filterIsInstance<ScheduleItem.TaskItem>().any {
            time.isAfter(it.startTime.minusMinutes(1)) &&
                    time.isBefore(it.endTime)
        }
    }

    inner class EmptyHourViewHolder(private val binding: ItemScheduleEmptyHourBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItem.EmptyHour, position: Int) {
            binding.textTime.text = item.time.format(timeFormatter)

            // Mostrar el indicador de tiempo actual
            val showIndicator = shouldShowTimeIndicator(position)
            binding.indicatorCurrentTime.visibility = if (showIndicator) View.VISIBLE else View.GONE

            // Posicionar el indicador en la posición exacta dentro de la franja de 30 minutos
            if (showIndicator && currentTime != null) {
                // Calcular la posición relativa dentro de la franja (0-1)
                val slotStartMinutes = item.time.hour * 60 + item.time.minute
                val currentMinutes = currentTime!!.hour * 60 + currentTime!!.minute
                val relativePosition = (currentMinutes - slotStartMinutes) / 30f

                // Aplicar esta posición al margen superior del indicador
                val params = binding.indicatorCurrentTime.layoutParams as ViewGroup.MarginLayoutParams
                val totalHeight = binding.root.height ?: 60 // Altura por defecto si no está disponible
                params.topMargin = (totalHeight * relativePosition).toInt()
                binding.indicatorCurrentTime.layoutParams = params
            }
        }
    }

    inner class TaskViewHolder(private val binding: ItemScheduleTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItem.TaskItem, position: Int) {
            // Mostrar la hora de inicio y el título
            binding.textTime.text = "${item.startTime.format(timeFormatter)} - ${item.endTime.format(timeFormatter)}"
            binding.textTaskTitle.text = item.title

            // Calcular la altura de la tarjeta basada en la duración de la tarea
            val durationMinutes = ChronoUnit.MINUTES.between(item.startTime, item.endTime)

            // Ajustamos la altura: cada 15 minutos son aprox. 20dp, con un mínimo de 60dp
            val calculatedHeight = (durationMinutes * 4 / 3).toInt()
            val heightDp = min(calculatedHeight, 500).coerceAtLeast(60)

            val params = binding.cardTask.layoutParams
            params.height = (heightDp * binding.root.resources.displayMetrics.density).toInt()
            binding.cardTask.layoutParams = params

            // Mostrar el indicador de tiempo actual
            val showIndicator = shouldShowTimeIndicator(position)
            binding.indicatorCurrentTime.visibility = if (showIndicator) View.VISIBLE else View.GONE

            // Posicionar el indicador en la posición exacta dentro de la tarea
            if (showIndicator && currentTime != null) {
                // Calcular la posición relativa dentro de la tarea (0-1)
                val taskStartMinutes = item.startTime.hour * 60 + item.startTime.minute
                val taskEndMinutes = item.endTime.hour * 60 + item.endTime.minute
                val currentMinutes = currentTime!!.hour * 60 + currentTime!!.minute
                val relativePosition = (currentMinutes - taskStartMinutes).toFloat() /
                        (taskEndMinutes - taskStartMinutes)

                // Aplicar esta posición al margen superior del indicador
                val params = binding.indicatorCurrentTime.layoutParams as ViewGroup.MarginLayoutParams
                val totalHeight = binding.cardTask.height ?: heightDp // Altura por defecto si no está disponible
                params.topMargin = (totalHeight * relativePosition).toInt()
                binding.indicatorCurrentTime.layoutParams = params
            }

            // Solo permitir clickear si la tarea está antes de la hora actual
            val isClickable = currentTime != null &&
                    (item.endTime.isBefore(currentTime) ||
                            item.endTime.equals(currentTime))

            if (isClickable) {
                binding.cardTask.setOnClickListener {
                    if (!item.isCompleted) {
                        val bounceAnimation = AnimationUtils.loadAnimation(
                            binding.root.context,
                            R.anim.bounce_animation
                        )
                        binding.cardTask.startAnimation(bounceAnimation)
                    }
                    onTaskClicked(item.id)
                }
            } else {
                binding.cardTask.setOnClickListener(null)
            }
        }
    }
}

sealed class ScheduleItem {
    data class EmptyHour(val time: LocalTime) : ScheduleItem()
    data class TaskItem(
        val id: Int,
        val title: String,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val isCompleted: Boolean
    ) : ScheduleItem()
}