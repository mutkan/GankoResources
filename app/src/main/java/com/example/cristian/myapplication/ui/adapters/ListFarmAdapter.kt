package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Finca
import com.example.cristian.myapplication.databinding.TemplateFarmBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ListFarmAdapter @Inject constructor(): RecyclerView.Adapter<ListFarmViewHolder>(){

    val clickDeleteFarm: PublishSubject<Finca> = PublishSubject.create()
    val clickEditFarm: PublishSubject<String> = PublishSubject.create()
    val clickFarm:PublishSubject<String> = PublishSubject.create()
    var farms:List<Finca> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFarmViewHolder = ListFarmViewHolder(parent.inflate(R.layout.template_farm))

    override fun getItemCount(): Int = farms.size

    override fun onBindViewHolder(holder: ListFarmViewHolder, position: Int) {
        holder.binding.farm = farms[position]
        holder.binding.clickDeleteFarm = clickDeleteFarm
        holder.binding.clickEditFarm = clickEditFarm
        holder.binding.clickFarm = clickFarm
    }
}

class ListFarmViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
    val binding: TemplateFarmBinding = DataBindingUtil.bind(itemView)!!
}
