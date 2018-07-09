package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.databinding.TemplateMovementUsedBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject

class MovementUsedAdapter:RecyclerView.Adapter<MovementUsedAdapter.MovementHolder>() {

    val onClickMeadow:PublishSubject<Pradera> = PublishSubject.create()

    var data:MutableList<Pradera> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementHolder =
            MovementHolder(parent.inflate(R.layout.template_movement_used))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MovementHolder, position: Int) =
            holder.bind(data[position],onClickMeadow)

    class MovementHolder(view: View):RecyclerView.ViewHolder(view){
        val binding: TemplateMovementUsedBinding = DataBindingUtil.bind(view)!!
        fun bind(meadow:Pradera,onClickMeadow:PublishSubject<Pradera>){
            binding.meadow = meadow
            binding.onClickMeadow = onClickMeadow
        }
    }
}