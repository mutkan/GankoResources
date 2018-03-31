package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Manage
import com.example.cristian.myapplication.databinding.TemplateListManagementBovineBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class ListManagementBovineAdapter @Inject constructor():RecyclerView.Adapter<ListManagementBovineAdapter.ListManagementBovineHolder>() {

    var data:List<Manage> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListManagementBovineHolder =
            ListManagementBovineHolder(parent!!.inflate(R.layout.template_list_management_bovine))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListManagementBovineHolder?, position: Int) =
            holder!!.bind(data[position])

    class ListManagementBovineHolder(itemList:View):RecyclerView.ViewHolder(itemList){
        val binding:TemplateListManagementBovineBinding = DataBindingUtil.bind(itemList)!!
        fun bind(manejo:Manage){
            binding.manejo = manejo
        }
    }

}