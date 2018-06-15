package com.example.cristian.myapplication.ui.groups.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.TemplateSelectedBinding
import com.example.cristian.myapplication.di.ActivityScope
import com.example.cristian.myapplication.util.inflate
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

    override fun onBindViewHolder(holder: SelectedHolder, position: Int) {
        holder.bind(data[position], onRemove, position)
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedAdapter.SelectedHolder = SelectedAdapter.SelectedHolder(parent.inflate(R.layout.template_selected))


    class SelectedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateSelectedBinding = TemplateSelectedBinding.bind(itemView)

        fun bind(bovine: Bovino, remove: PublishSubject<Int>, pos:Int) = binding.run {
            bovino = bovine
            onRemove = remove
            position = pos
        }
    }
}