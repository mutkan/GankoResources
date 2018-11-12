package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Sanidad
import com.ceotic.ganko.databinding.TemplateNextHealthBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class NextHealthAdapter @Inject constructor(): RecyclerView.Adapter<NextHealthAdapter.NextHealthHolder>(){

    val clickApply: PublishSubject<Sanidad> = PublishSubject.create()

    val clickSkip  : PublishSubject<Sanidad> = PublishSubject.create()

    var health: List<Sanidad> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextHealthHolder
     = NextHealthHolder(parent.inflate(R.layout.template_next_health))

    override fun getItemCount(): Int = health.size

    override fun onBindViewHolder(holder: NextHealthHolder, position: Int) {
        holder.binding.sanidad = health[position]
        holder.binding.clickApply = clickApply
        holder.binding.clickSkip = clickSkip
    }


    class NextHealthHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding: TemplateNextHealthBinding = DataBindingUtil.bind(itemView)!!
    }
}