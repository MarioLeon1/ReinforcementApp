package com.example.reinforcement.ui.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.reinforcement.R
import com.example.reinforcement.data.model.TodoTask
import com.example.reinforcement.databinding.ItemTodoTaskBinding

class TodoAdapter(
    private val onTaskClicked: (TodoTask) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var tasks: List<TodoTask> = emptyList()

    fun submitList(newTasks: List<TodoTask>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    inner class TodoViewHolder(private val binding: ItemTodoTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TodoTask) {
            binding.textTodoTitle.text = task.title
            
            // Cambiar el aspecto según si está completado o no
            if (task.isCompleted) {
                binding.textTaskBullet.text = "●"
                binding.textTaskBullet.setTextColor(binding.root.context.getColor(R.color.green_success))
                binding.textTodoTitle.setTextColor(binding.root.context.getColor(R.color.secondary_text))
            } else {
                binding.textTaskBullet.text = "○"
                binding.textTaskBullet.setTextColor(binding.root.context.getColor(R.color.primary_text))
                binding.textTodoTitle.setTextColor(binding.root.context.getColor(R.color.primary_text))
            }

            binding.root.setOnClickListener {
                if (!task.isCompleted) {
                    // Animar el cambio de estado
                    val bounceAnimation = AnimationUtils.loadAnimation(
                        binding.root.context,
                        R.anim.bounce_animation
                    )
                    binding.textTaskBullet.startAnimation(bounceAnimation)
                }
                onTaskClicked(task)
            }
        }
    }
}