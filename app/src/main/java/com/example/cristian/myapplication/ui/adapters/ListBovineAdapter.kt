package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.TemplateBovineBinding
import com.example.cristian.myapplication.util.inflate

class ListBovineAdapter:RecyclerView.Adapter<ListBovineAdapter.ListBovineHolder>() {

    var bovines: List<Bovino> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListBovineHolder
            = ListBovineHolder(parent.inflate(R.layout.template_bovine))

    override fun onBindViewHolder(holder: ListBovineHolder, position: Int) {
        holder.binding.bovino = bovines[position]
    }

    override fun getItemCount(): Int = bovines.size

    class ListBovineHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding: TemplateBovineBinding = DataBindingUtil.bind(itemView)!!
    }



}