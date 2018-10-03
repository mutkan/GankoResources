package com.example.cristian.myapplication.ui.groups.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.TemplateSelectBinding
import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@FragmentScope
class SelectAdapter @Inject constructor() : RecyclerView.Adapter<SelectAdapter.SelectHolder>() {

    val onSelectBovine: PublishSubject<Bovino> = PublishSubject.create()
    var data: MutableList<Bovino> = mutableListOf()
        set(value) {
            field = value
            page = 0
            notifyDataSetChanged()
        }
    var selecteds:HashMap<String, Boolean> = HashMap()
    var page = 0
    val nextPage:PublishSubject<Int> = PublishSubject.create()

    override fun onBindViewHolder(holder: SelectHolder, position: Int) {
        holder.bind(data[position], this, selecteds[data[position]._id] ?: false)
        if(position  == data.lastIndex && data.size % 30 == 0){
            nextPage.onNext((data.size / 30) + 1)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectHolder = SelectHolder(parent.inflate(R.layout.template_select))

    fun select(bovine: Bovino){
        val prev = selecteds[bovine._id] ?: false
        if(prev) selecteds.remove(bovine._id)
        else selecteds[bovine._id!!] = true
        notifyDataSetChanged()
        onSelectBovine.onNext(bovine)
    }

    fun addData(data:List<Bovino>){
        if(this.data.isEmpty()){
            this.data = data.toMutableList()
        }else{
            this.data.addAll(data)
            notifyDataSetChanged()
        }
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