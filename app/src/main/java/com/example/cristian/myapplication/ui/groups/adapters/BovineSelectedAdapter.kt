package com.example.cristian.myapplication.ui.groups.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.TemplateBovinesSelectedBinding
import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.util.inflate
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