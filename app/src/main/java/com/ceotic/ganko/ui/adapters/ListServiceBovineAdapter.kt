package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Servicio
import com.ceotic.ganko.databinding.TemplateBirthBinding
import com.ceotic.ganko.databinding.TemplateListServiceBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ListServiceBovineAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var services: List<Servicio> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    val clickAddDiagnostico: PublishSubject<Pair<Servicio, Int>> = PublishSubject.create()
    val clickAddNovedad: PublishSubject<Pair<Servicio, Int>> = PublishSubject.create()
    val clickAddParto: PublishSubject<Pair<Servicio, Int>> = PublishSubject.create()

    override fun getItemViewType(position: Int): Int {
        return if (services[position].parto == null) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        0 -> ServiceViewHolder(parent.inflate(R.layout.template_list_service))
        else -> BirthViewHolder(parent.inflate(R.layout.template_birth))
    }

    override fun getItemCount(): Int = services.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ServiceViewHolder -> {
                holder.binding.service = services[position]
                holder.binding.position = position
                holder.binding.clickAddDiagnostico = clickAddDiagnostico
                holder.binding.clickAddNovedad = clickAddNovedad
                holder.binding.clickAddParto = clickAddParto
            }
            is BirthViewHolder -> {
                holder.binding.servicio = services[position]
            }
        }
    }
}

class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: TemplateListServiceBinding = DataBindingUtil.bind(itemView)!!
}

class BirthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: TemplateBirthBinding = DataBindingUtil.bind(itemView)!!
}
