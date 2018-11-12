package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.RegistroManejo
import com.ceotic.ganko.databinding.TemplateListManagementBovineBinding
import com.ceotic.ganko.util.inflate
import javax.inject.Inject

class ListManagementBovineAdapter @Inject constructor():RecyclerView.Adapter<ListManagementBovineAdapter.ListManagementBovineHolder>() {

    var data:List<RegistroManejo> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListManagementBovineHolder =
            ListManagementBovineHolder(parent.inflate(R.layout.template_list_management_bovine))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListManagementBovineHolder, position: Int) =
            holder!!.bind(data[position])

    class ListManagementBovineHolder(itemList:View):RecyclerView.ViewHolder(itemList){
        val binding:TemplateListManagementBovineBinding = DataBindingUtil.bind(itemList)!!
        fun bind(manejo: RegistroManejo){
            binding.manejo = manejo
        }
    }

}