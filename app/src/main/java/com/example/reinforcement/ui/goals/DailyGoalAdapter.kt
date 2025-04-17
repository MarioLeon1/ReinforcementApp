package com.example.reinforcement.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.reinforcement.R
import com.example.reinforcement.data.model.DailyGoal
import com.example.reinforcement.databinding.ItemDailyGoalBinding

class DailyGoalAdapter(
    private val onGoalClicked: (DailyGoal) -> Unit
) : RecyclerView.Adapter<DailyGoalAdapter.GoalViewHolder>() {

    private var goals: List<DailyGoal> = emptyList()

    fun submitList(newGoals: List<DailyGoal>) {
        goals = newGoals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemDailyGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount(): Int = goals.size

    inner class GoalViewHolder(private val binding: ItemDailyGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: DailyGoal) {
            binding.textGoalTitle.text = goal.title

            // Cambiar el aspecto según si está completado o no
            if (goal.isCompleted) {
                binding.textGoalBullet.text = "●"
                binding.textGoalBullet.setTextColor(binding.root.context.getColor(R.color.green_success))
                binding.textGoalTitle.setTextColor(binding.root.context.getColor(R.color.secondary_text))
            } else {
                binding.textGoalBullet.text = "○"
                binding.textGoalBullet.setTextColor(binding.root.context.getColor(R.color.primary_text))
                binding.textGoalTitle.setTextColor(binding.root.context.getColor(R.color.primary_text))
            }

            binding.root.setOnClickListener {
                if (!goal.isCompleted) {
                    // Animar el cambio de estado
                    val bounceAnimation = AnimationUtils.loadAnimation(binding.root.context, R.anim.bounce_animation)
                    binding.textGoalBullet.startAnimation(bounceAnimation)
                }
                onGoalClicked(goal)
            }
        }
    }
}