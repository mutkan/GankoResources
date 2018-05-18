package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Manage
import com.example.cristian.myapplication.data.models.Straw
import com.example.cristian.myapplication.databinding.TemplateStrawBinding
import com.example.cristian.myapplication.util.inflate
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

    }


    class StrawHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateStrawBinding? = DataBindingUtil.bind(itemView)
    }
}