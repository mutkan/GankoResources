package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Servicio
import com.example.cristian.myapplication.databinding.TemplateServiceBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class ListServiceAdapter @Inject constructor() : RecyclerView.Adapter<ServiceViewHolder>() {

    var services: List<Servicio> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder = ServiceViewHolder(parent.inflate(R.layout.template_service))

    override fun getItemCount(): Int = services.size

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.binding.service = services[position]
    }
}

class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: TemplateServiceBinding = DataBindingUtil.bind(itemView)!!

}
