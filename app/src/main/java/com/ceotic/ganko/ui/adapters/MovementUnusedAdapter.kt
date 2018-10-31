package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Pradera
import com.ceotic.ganko.databinding.TemplateMovementUnusedBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject

class MovementUnusedAdapter:RecyclerView.Adapter<MovementUnusedAdapter.MovementHolder>() {

    val onClickMeadow:PublishSubject<Pradera> = PublishSubject.create()

    var data:List<Pradera> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementHolder =
            MovementHolder(parent.inflate(R.layout.template_movement_unused))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MovementHolder, position: Int) =
            holder.bind(data[position],onClickMeadow)

    class MovementHolder(view: View):RecyclerView.ViewHolder(view){
        val binding:TemplateMovementUnusedBinding = DataBindingUtil.bind(view)!!
        fun bind(meadow:Pradera,onClickMeadow:PublishSubject<Pradera>){
            binding.meadow = meadow
            binding.onClickMeadow = onClickMeadow
        }
    }
}