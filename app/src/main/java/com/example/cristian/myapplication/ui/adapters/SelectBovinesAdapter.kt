package com.example.cristian.myapplication.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.TemplateSelectFeedBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class SelectBovinesAdapter @Inject constructor(): RecyclerView.Adapter<SelectBovinesAdapter.SelectBovinesHolder>(){

    val onSelectBovine:PublishSubject<BovineSelect> = PublishSubject.create()
    var data: List<BovineSelect> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: SelectBovinesHolder, position: Int) {
        holder.bind(data[position], onSelectBovine)
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectBovinesHolder
            = SelectBovinesHolder(parent.inflate(R.layout.template_select_feed))



    class SelectBovinesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : TemplateSelectFeedBinding = TemplateSelectFeedBinding.bind(itemView)

        fun bind(bovine:BovineSelect, selectedBovine:PublishSubject<BovineSelect>){
            binding.bovino = bovine
            binding.onSelect = selectedBovine
        }
    }
}


class BovineSelect(val bovine: Bovino, var selected:Boolean = false)
