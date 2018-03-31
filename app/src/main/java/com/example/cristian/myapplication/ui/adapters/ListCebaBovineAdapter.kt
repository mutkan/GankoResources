package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Ceba
import com.example.cristian.myapplication.databinding.TemplateListCebaBovineBinding
import com.example.cristian.myapplication.util.inflate

class ListCebaBovineAdapter:RecyclerView.Adapter<ListCebaBovineAdapter.ListCebaBovineHolder>() {

    var data:List<Ceba> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListCebaBovineHolder =
            ListCebaBovineHolder(parent!!.inflate(R.layout.template_list_ceba_bovine))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListCebaBovineHolder?, position: Int) =
            holder!!.bind(data[position])


    class ListCebaBovineHolder(listItem: View):RecyclerView.ViewHolder(listItem){
        val binding:TemplateListCebaBovineBinding = DataBindingUtil.bind(listItem)!!
        fun bind(ceba: Ceba){
            binding.ceba = ceba
        }
    }
}