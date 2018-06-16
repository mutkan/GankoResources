package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.data.models.SalidaLeche
import com.example.cristian.myapplication.databinding.TemplateListMilkBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class MilkAdapter @Inject constructor(): RecyclerView.Adapter<MilkAdapter.MilkHolder>(){

    var milk: List<SalidaLeche> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilkHolder
            = MilkHolder(parent.inflate(R.layout.template_list_milk))

    override fun getItemCount(): Int = milk.size

    override fun onBindViewHolder(holder: MilkHolder, position: Int) {
        holder.binding?.milk = milk[position]
    }

    class MilkHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding: TemplateListMilkBinding? = DataBindingUtil.bind(itemView)
    }
}