package com.example.reinforcement.ui.points

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reinforcement.R
import com.example.reinforcement.data.model.PointEntry
import com.example.reinforcement.databinding.ItemPointEntryBinding

class PointEntryAdapter : RecyclerView.Adapter<PointEntryAdapter.PointEntryViewHolder>() {

    private var entries: List<PointEntry> = emptyList()

    fun submitList(newEntries: List<PointEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointEntryViewHolder {
        val binding = ItemPointEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PointEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointEntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount(): Int = entries.size

    inner class PointEntryViewHolder(private val binding: ItemPointEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: PointEntry) {
            binding.textPointDescription.text = entry.description
            
            // Formatear valor de puntos con signo
            val pointsText = if (entry.points > 0) {
                "+${entry.points}"
            } else {
                "${entry.points}"
            }
            binding.textPointValue.text = pointsText
            
            // Colorear según si es positivo o negativo
            val textColor = if (entry.points >= 0) {
                ContextCompat.getColor(binding.root.context, R.color.green_success)
            } else {
                ContextCompat.getColor(binding.root.context, android.R.color.holo_red_dark)
            }
            binding.textPointValue.setTextColor(textColor)
        }
    }
}