package com.example.reinforcement.ui.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
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

                    // Mostrar animación de puntos
                    showPointsAnimation(binding.root)
                }
                onGoalClicked(goal)
            }
        }

        private fun showPointsAnimation(rootView: View) {
            // Inflar la vista de animación
            val inflater = LayoutInflater.from(rootView.context)
            val pointsAnimView = inflater.inflate(R.layout.points_animation_view, null) as TextView

            // Configurar la posición de la vista de animación
            val rootLayout = rootView.rootView as ViewGroup
            val layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            rootLayout.addView(pointsAnimView, layoutParams)

            // Posicionar la vista de animación junto al objetivo
            val location = IntArray(2)
            binding.textGoalTitle.getLocationInWindow(location)

            // Posicionamos la animación justo a la derecha del texto del objetivo con un pequeño margen
            pointsAnimView.x = location[0].toFloat() + binding.textGoalTitle.width / 2
            pointsAnimView.y = location[1].toFloat()

            // Iniciar la animación
            val animation = AnimationUtils.loadAnimation(rootView.context, R.anim.points_animation)
            pointsAnimView.alpha = 1f
            pointsAnimView.startAnimation(animation)

            // Eliminar la vista después de que termine la animación
            rootView.postDelayed({
                rootLayout.removeView(pointsAnimView)
            }, 1100) // Un poco más que la duración total de la animación
        }
    }
}