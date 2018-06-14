package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.TemplateZealBinding
import com.example.cristian.myapplication.util.inflate
import java.util.*
import javax.inject.Inject

class ListZealAdapter @Inject constructor(): RecyclerView.Adapter<ZealViewHolder>() {

    var zeals: List<Date> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZealViewHolder = ZealViewHolder(parent.inflate(R.layout.template_zeal))

    override fun getItemCount(): Int = zeals.size

    override fun onBindViewHolder(holder: ZealViewHolder, position: Int) {
        holder.binding.zeal = zeals[position]
    }
}

class ZealViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
    val binding: TemplateZealBinding = DataBindingUtil.bind(itemView)!!
}