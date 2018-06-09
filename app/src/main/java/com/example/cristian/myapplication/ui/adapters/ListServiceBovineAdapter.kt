package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Servicio
import com.example.cristian.myapplication.databinding.TemplateBirthBinding
import com.example.cristian.myapplication.databinding.TemplateListServiceBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class ListServiceAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var services: List<Servicio> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return if(services[position].parto!!.isEmpty()) 0 else 1
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType){
        0 -> ServiceViewHolder(parent.inflate(R.layout.template_list_service))
        else -> BirthViewHolder(parent.inflate(R.layout.template_birth))
    }

    override fun getItemCount(): Int = services.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
     when(holder){
         is ServiceViewHolder ->{
             holder.binding.service = services[position]
         }
         is BirthViewHolder -> {
             holder.binding.parto = services[position].parto!!.first()
         }
     }
    }
}

class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: TemplateListServiceBinding = DataBindingUtil.bind(itemView)!!
}

class BirthViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val binding: TemplateBirthBinding = DataBindingUtil.bind(itemView)!!
}
