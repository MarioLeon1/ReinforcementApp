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
            is ScheduleItem.EmptyHour -> (holder as EmptyHourViewHolder).bind(item)
            is ScheduleItem.TaskItem -> (holder as TaskViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class EmptyHourViewHolder(private val binding: ItemScheduleEmptyHourBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItem.EmptyHour) {
            binding.textTime.text = item.time.format(timeFormatter)
            
            // Mostrar el indicador de tiempo actual si corresponde
            val showIndicator = currentTime != null && 
                               (item.time.hour == currentTime?.hour && 
                                item.time.minute <= currentTime?.minute ?: 0)
            
            binding.indicatorCurrentTime.visibility = if (showIndicator) View.VISIBLE else View.GONE
        }
    }

    inner class TaskViewHolder(private val binding: ItemScheduleTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItem.TaskItem) {
            binding.textTime.text = item.startTime.format(timeFormatter)
            binding.textTaskTitle.text = item.title
            
            // Cambiar color según si está completada
            val cardColor = if (item.isCompleted) {
                binding.root.context.getColor(R.color.task_completed)
            } else {
                binding.root.context.getColor(R.color.task_uncompleted)
            }
            binding.cardTask.setCardBackgroundColor(cardColor)
            
            // Mostrar el indicador de tiempo actual si corresponde
            val showIndicator = currentTime != null && 
                               item.startTime.isBefore(currentTime) && 
                               item.endTime.isAfter(currentTime)
            
            binding.indicatorCurrentTime.visibility = if (showIndicator) View.VISIBLE else View.GONE
            
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