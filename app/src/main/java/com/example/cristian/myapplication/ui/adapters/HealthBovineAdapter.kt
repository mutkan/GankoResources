package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.databinding.TemplateListHealthBovineBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class HealthBovineAdapter @Inject constructor(): RecyclerView.Adapter<HealthBovineAdapter.HealthBovineHolder>(){

    var health: List<Sanidad> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = health.size

    override fun onBindViewHolder(holder: HealthBovineHolder, position: Int) {
        holder.binding.sanidad = health[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthBovineHolder =
            HealthBovineHolder(parent.inflate(R.layout.template_list_health_bovine))

    class HealthBovineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateListHealthBovineBinding = DataBindingUtil.bind(itemView)!!
    }
}