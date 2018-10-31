package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Mantenimiento
import com.ceotic.ganko.databinding.TemplateMaintenanceBinding
import com.ceotic.ganko.util.inflate

class MaintenanceAdapter:RecyclerView.Adapter<MaintenanceAdapter.MaintenanceHolder>() {

    var data:MutableList<Mantenimiento> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceHolder =
            MaintenanceHolder(parent.inflate(R.layout.template_maintenance))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MaintenanceHolder, position: Int) =
            holder.bind(data[position])


    class MaintenanceHolder(view:View):RecyclerView.ViewHolder(view){
        val binding:TemplateMaintenanceBinding = DataBindingUtil.bind(view)!!
        fun bind(maintenance:Mantenimiento){
            binding.maintenance = maintenance
        }
    }
}