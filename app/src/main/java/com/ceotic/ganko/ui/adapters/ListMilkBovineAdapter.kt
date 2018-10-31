package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Produccion
import com.ceotic.ganko.databinding.TemplateListMilkBovineBinding
import com.ceotic.ganko.util.inflate
import javax.inject.Inject

class ListMilkBovineAdapter @Inject constructor():RecyclerView.Adapter<ListMilkBovineAdapter.ListMilkBovineHolder>() {


    var data:List<Produccion> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListMilkBovineHolder =
            ListMilkBovineHolder(parent.inflate(R.layout.template_list_milk_bovine))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListMilkBovineHolder, position: Int){
            holder.bind(data[position])
    }

    class ListMilkBovineHolder(listItem: View):RecyclerView.ViewHolder(listItem){
        val binding : TemplateListMilkBovineBinding = DataBindingUtil.bind(listItem)!!
        fun bind(produccion: Produccion){
            binding.produccion = produccion
            binding.jornadaListMilkBovine.text = if(produccion.jornada == "Manana") "Mañana" else produccion.jornada
        }
    }
}
