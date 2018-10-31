package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Finca
import com.ceotic.ganko.databinding.TemplateFarmBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ListFarmAdapter @Inject constructor(): RecyclerView.Adapter<ListFarmViewHolder>(){

    val clickDeleteFarm: PublishSubject<Finca> = PublishSubject.create()
    val clickEditFarm: PublishSubject<Finca> = PublishSubject.create()
    val clickFarm:PublishSubject<Finca> = PublishSubject.create()
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
