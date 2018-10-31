package com.ceotic.ganko.ui.groups.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.databinding.TemplateSelectedBinding
import com.ceotic.ganko.di.ActivityScope
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@ActivityScope
class SelectedAdapter @Inject constructor() : RecyclerView.Adapter<SelectedAdapter.SelectedHolder>() {

    val onRemove: PublishSubject<Int> = PublishSubject.create()
    var data: MutableList<Bovino> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var editable:Boolean = true

    override fun onBindViewHolder(holder: SelectedHolder, position: Int) {
        holder.bind(data[position], onRemove, position, editable)
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedAdapter.SelectedHolder = SelectedAdapter.SelectedHolder(parent.inflate(R.layout.template_selected))


    class SelectedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateSelectedBinding = TemplateSelectedBinding.bind(itemView)

        fun bind(bovine: Bovino, remove: PublishSubject<Int>, pos:Int, edit:Boolean) = binding.run {
            bovino = bovine
            onRemove = remove
            position = pos
            editable = edit
        }
    }
}