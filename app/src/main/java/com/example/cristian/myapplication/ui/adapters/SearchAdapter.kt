package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.*
import com.example.cristian.myapplication.databinding.*
import com.example.cristian.myapplication.ui.search.SearchActivity
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class SearchAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: MutableList<SearchActivity.SearchType> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int =
            data[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                SearchActivity.MANAGE -> SearchManageHolder(parent.inflate(R.layout.template_list_management_bovine))
                SearchActivity.VACCINE -> SearchVaccineHolder(parent.inflate(R.layout.template_list_vaccine_bovine))
                SearchActivity.HEALTH -> SearchHealthHolder(parent.inflate(R.layout.template_list_health_bovine))
                SearchActivity.BOVINE -> SearchBovineHolder(parent.inflate(R.layout.template_bovine))
                SearchActivity.STRAW -> SearchStrawHolder(parent.inflate(R.layout.template_straw))
                else -> SearchFeedHolder(parent.inflate(R.layout.template_feed))
            }


    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchFeedHolder -> holder.bind(data[position].item as RegistroAlimentacion)
            is SearchStrawHolder -> holder.bind(data[position].item as Straw)
            is SearchBovineHolder -> holder.bind(data[position].item as Bovino)
            is SearchHealthHolder -> holder.bind(data[position].item as Sanidad)
            is SearchVaccineHolder -> holder.bind(data[position].item as RegistroVacuna)
            is SearchManageHolder -> holder.bind(data[position].item as RegistroManejo)
        }
    }

    class SearchFeedHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: TemplateFeedBinding = DataBindingUtil.bind(view)!!
        fun bind(alimentacion: RegistroAlimentacion) {
            binding.feeding = alimentacion
        }
    }

    class SearchStrawHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: TemplateStrawBinding = DataBindingUtil.bind(view)!!
        fun bind(pajilla: Straw) {
            binding.straw = pajilla
        }
    }

    class SearchBovineHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: TemplateBovineBinding = DataBindingUtil.bind(view)!!
        fun bind(bovino: Bovino) {
            binding.bovino = bovino
        }
    }

    class SearchHealthHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: TemplateListHealthBovineBinding = DataBindingUtil.bind(view)!!
        fun bind(sanidad: Sanidad) {
            binding.sanidad = sanidad
        }
    }

    class SearchVaccineHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: TemplateListVaccineBovineBinding = DataBindingUtil.bind(view)!!
        fun bind(vacuna: RegistroVacuna) {
            binding.vacuna = vacuna
        }
    }

    class SearchManageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: TemplateListManagementBovineBinding = DataBindingUtil.bind(view)!!
        fun bind(manejo: RegistroManejo) {
            binding.manejo = manejo
        }
    }
}