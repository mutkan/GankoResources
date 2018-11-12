package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Movimiento
import com.ceotic.ganko.databinding.TemplateListMovementBovineBinding
import com.ceotic.ganko.util.inflate
import javax.inject.Inject

class ListMovementBovineAdapter @Inject constructor(): RecyclerView.Adapter<ListMovementBovineAdapter.ListMovementBovineHolder>() {

    var data:List<Movimiento> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListMovementBovineHolder =
            ListMovementBovineHolder(parent.inflate(R.layout.template_list_movement_bovine))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListMovementBovineHolder, position: Int) =
            holder!!.bind(data[position])

    class ListMovementBovineHolder(itemList: View): RecyclerView.ViewHolder(itemList){
        val binding:TemplateListMovementBovineBinding = DataBindingUtil.bind(itemList)!!
        fun bind(movimiento: Movimiento){
            binding.movimiento = movimiento
        }
    }

}