package com.s8125078.assignment2.ui.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.s8125078.assignment2.R
import com.s8125078.assignment2.data.model.Entity

class EntityAdapter(
    private val onEntityClick: (Entity) -> Unit
) : RecyclerView.Adapter<EntityAdapter.EntityViewHolder>() {

    private var entities: List<Entity> = emptyList()

    // the whole list is replaced at once and it's only a handful of items,
    // so notifyDataSetChanged is fine here
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newEntities: List<Entity>) {
        entities = newEntities
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entity, parent, false)
        return EntityViewHolder(view, onEntityClick)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    override fun getItemCount(): Int = entities.size

    class EntityViewHolder(
        itemView: View,
        private val onEntityClick: (Entity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val periodText: TextView = itemView.findViewById(R.id.tvEntityPeriod)
        private val titleText: TextView = itemView.findViewById(R.id.tvEntityTitle)
        private val summaryText: TextView = itemView.findViewById(R.id.tvEntitySummary)

        fun bind(entity: Entity) {
            periodText.isVisible = entity.period != null
            periodText.text = entity.period
            titleText.text = entity.title
            summaryText.isVisible = entity.summary.isNotEmpty()
            summaryText.text = entity.summary
            itemView.setOnClickListener { onEntityClick(entity) }
        }
    }
}
