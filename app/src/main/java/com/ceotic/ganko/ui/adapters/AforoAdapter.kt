package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Aforo
import com.ceotic.ganko.databinding.TemplateAforoBinding
import com.ceotic.ganko.util.inflate

class AforoAdapter:RecyclerView.Adapter<AforoAdapter.AforoHolder>() {

    var data:MutableList<Aforo> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AforoHolder =
            AforoHolder(parent.inflate(R.layout.template_aforo))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: AforoHolder, position: Int) =
            holder.bind(data[position])

    class AforoHolder(view:View):RecyclerView.ViewHolder(view){
        val binding:TemplateAforoBinding = DataBindingUtil.bind(view)!!
        fun bind(aforo: Aforo){
            binding.aforo = aforo
        }
    }
}