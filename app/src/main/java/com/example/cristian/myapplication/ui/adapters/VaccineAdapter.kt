package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.databinding.TemplateNextVaccinesBinding
import com.example.cristian.myapplication.databinding.TemplateRecentVaccineBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class VaccineAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<RegistroVacuna> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var tipo: Int = TYPE_VACCINATIONS
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    val clickRevaccination: PublishSubject<RegistroVacuna> = PublishSubject.create()
    val clickSkipVaccine: PublishSubject<RegistroVacuna> = PublishSubject.create()

    override fun getItemViewType(position: Int): Int = tipo

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_VACCINATIONS -> VaccineViewHolder(parent.inflate(R.layout.template_recent_vaccine))
        else -> NextVaccinesViewHolder(parent.inflate(R.layout.template_next_vaccines))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VaccineViewHolder -> {
                holder.binding.vacuna = data[position]
            }
            is NextVaccinesViewHolder -> {
                holder.binding.vacuna = data[position]
                holder.binding.clickRevaccination = clickRevaccination
                holder.binding.clickSkipVaccine = clickSkipVaccine
            }
        }
    }

    companion object {
        const val TYPE_VACCINATIONS = 0
        const val TYPE_NEXT_VACCINATIONS = 1
    }
}

class VaccineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: TemplateRecentVaccineBinding = DataBindingUtil.bind(itemView)!!
}

class NextVaccinesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: TemplateNextVaccinesBinding = DataBindingUtil.bind(itemView)!!
}