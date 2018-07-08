package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.databinding.TemplateNextHealthBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class NextHealthAdapter @Inject constructor(): RecyclerView.Adapter<NextHealthAdapter.NextHealthHolder>(){

    var health: List<Sanidad> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    val clickApply: PublishSubject<Sanidad> = PublishSubject.create()

    val clickSkip: PublishSubject<Sanidad> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextHealthHolder
     = NextHealthHolder(parent.inflate(R.layout.template_next_health))

    override fun getItemCount(): Int = health.size

    override fun onBindViewHolder(holder: NextHealthHolder, position: Int) {
        holder.binding?.sanidad = health[position]
        holder.binding?.clickApply = clickApply
        holder.binding?.clickSkip = clickSkip
    }


    class NextHealthHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var binding: TemplateNextHealthBinding? = DataBindingUtil.bind(itemView)
    }
}