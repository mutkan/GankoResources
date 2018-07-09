package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.databinding.TemplateListVaccineBovineBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class ListVaccineBovineAdapter @Inject constructor():RecyclerView.Adapter<ListVaccineBovineAdapter.ListVaccineBovineHolder>() {

    var data:List<RegistroVacuna> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListVaccineBovineHolder =
            ListVaccineBovineHolder(parent.inflate(R.layout.template_list_vaccine_bovine))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListVaccineBovineHolder, position: Int) =
            holder.bind(data[position])

    class ListVaccineBovineHolder(itemList:View):RecyclerView.ViewHolder(itemList){
        val binding : TemplateListVaccineBovineBinding = DataBindingUtil.bind(itemList)!!
        fun bind(vacuna: RegistroVacuna){
            binding.vacuna = vacuna
        }
    }
}