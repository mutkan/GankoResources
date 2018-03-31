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

class ListHealthBovineAdapter @Inject constructor():RecyclerView.Adapter<ListHealthBovineAdapter.ListHealthBovineHolder>() {

    var data:List<Sanidad> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListHealthBovineHolder =
            ListHealthBovineHolder(parent!!.inflate(R.layout.template_list_health_bovine))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListHealthBovineHolder?, position: Int) =
            holder!!.bind(data[position])


    class ListHealthBovineHolder(itemList:View):RecyclerView.ViewHolder(itemList){
        val binding : TemplateListHealthBovineBinding = DataBindingUtil.bind(itemList)!!
        fun bind (sanidad: Sanidad){
            binding.sanidad = sanidad
        }
    }

}