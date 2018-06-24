package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.databinding.TemplateHealthBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class RecentHealthAdapter @Inject constructor(): RecyclerView.Adapter<RecentHealthAdapter.HealthHolder>() {

    var health: List<Sanidad> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthHolder = HealthHolder(parent.inflate(R.layout.template_recent_health))

    override fun getItemCount(): Int = health.size

    override fun onBindViewHolder(holder: HealthHolder, position: Int) {
        holder.binding?.health = health[position]
    }


    class HealthHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding: TemplateHealthBinding? = DataBindingUtil.bind(itemView)
    }
}