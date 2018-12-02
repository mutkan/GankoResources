package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Sanidad
import com.ceotic.ganko.databinding.TemplatePendingHealthBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class PendingHealthAdapter @Inject constructor():RecyclerView.Adapter<PendingHealthAdapter.PendingHealthHolder>(){

    val clickApply: PublishSubject<Sanidad> = PublishSubject.create()

    val clickSkip  : PublishSubject<Sanidad> = PublishSubject.create()

    var  pending :List<Sanidad> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingHealthHolder =
            PendingHealthHolder(parent.inflate(R.layout.template_pending_health))

    override fun getItemCount(): Int = pending.size

    override fun onBindViewHolder(holder: PendingHealthHolder, position: Int) {
            holder.binding?.sanidad = pending[position]
            holder.binding?.clickApply = clickApply
            holder.binding?.clickSkip = clickSkip
    }


    class PendingHealthHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var binding: TemplatePendingHealthBinding? = DataBindingUtil.bind(itemView)
    }
}