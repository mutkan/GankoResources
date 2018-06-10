package com.example.cristian.myapplication.ui.groups.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.TemplateSelectBinding
import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@FragmentScope
class SelectAdapter @Inject constructor() : RecyclerView.Adapter<SelectAdapter.SelectHolder>() {

    val onSelectBovine: PublishSubject<Bovino> = PublishSubject.create()
    var data: List<Bovino> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var selecteds:HashMap<String, Boolean> = HashMap()

    override fun onBindViewHolder(holder: SelectHolder, position: Int) {
        holder.bind(data[position], this, selecteds[data[position]._id] ?: false)
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectHolder = SelectHolder(parent.inflate(R.layout.template_select))

    fun select(bovine: Bovino){
        val prev = selecteds[bovine._id] ?: false
        selecteds[bovine._id!!] = !prev
        notifyDataSetChanged()
        onSelectBovine.onNext(bovine)
    }

    class SelectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateSelectBinding = TemplateSelectBinding.bind(itemView)

        fun bind(bovine: Bovino, adapter: SelectAdapter, select:Boolean) = binding.run {
            bovino = bovine
            handler = adapter
            selected = select
        }
    }
}