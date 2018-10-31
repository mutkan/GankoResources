package com.ceotic.ganko.ui.groups.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.databinding.TemplateBovinesSelectedBinding
import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.util.inflate
import javax.inject.Inject

@FragmentScope
class BovineSelectedAdapter @Inject constructor() : RecyclerView.Adapter<BovineSelectedHolder>() {

    var selecteds: List<Bovino> = emptyList()
    var noSelecteds: List<Bovino> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BovineSelectedHolder =
            BovineSelectedHolder(parent.inflate(R.layout.template_bovines_selected))

    override fun getItemCount(): Int = selecteds.size + noSelecteds.size

    override fun onBindViewHolder(holder: BovineSelectedHolder, position: Int) {
        val bovine = if (position < selecteds.size) selecteds[position]
        else noSelecteds[position - selecteds.size]
        val selected = position < selecteds.size
        holder.bind(bovine, selected)
    }


}

class BovineSelectedHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = TemplateBovinesSelectedBinding.bind(view)

    fun bind(bovine: Bovino, selected: Boolean) {
        binding.bovino = bovine
        binding.selected = selected
    }

}