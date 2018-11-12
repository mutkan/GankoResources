package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Straw
import com.ceotic.ganko.databinding.TemplateStrawBinding
import com.ceotic.ganko.util.inflate
import javax.inject.Inject

class StrawAdapter @Inject constructor(): RecyclerView.Adapter<StrawAdapter.StrawHolder>(){

    var straw: List<Straw> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrawHolder
            = StrawHolder(parent.inflate(R.layout.template_straw))

    override fun getItemCount(): Int = straw.size

    override fun onBindViewHolder(holder: StrawHolder, position: Int) {
        holder.binding?.straw  = straw[position]
    }


    class StrawHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateStrawBinding? = DataBindingUtil.bind(itemView)
    }
}