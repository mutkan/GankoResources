package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.MeadowAlarm
import com.ceotic.ganko.databinding.TemplateMeadowAlertBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject

class MeadowAlertAdapter:RecyclerView.Adapter<MeadowAlertAdapter.MeadowAlertHolder>() {


    val onClickDelete:PublishSubject<MeadowAlarm> = PublishSubject.create()
    var data:MutableList<MeadowAlarm> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeadowAlertHolder =
            MeadowAlertHolder(parent.inflate(R.layout.template_meadow_alert))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MeadowAlertHolder, position: Int) =
            holder.bind(data[position],onClickDelete)

    class MeadowAlertHolder(view:View):RecyclerView.ViewHolder(view){
        val binding:TemplateMeadowAlertBinding = DataBindingUtil.bind(view)!!
        fun bind(alarm:MeadowAlarm,onClickDelete:PublishSubject<MeadowAlarm>){
            binding.alert = alarm
            if(!alarm.wasShowed!!){
                binding.deleteMeadowAlarm.visibility = View.VISIBLE
                binding.onClickDelete = onClickDelete
            }
        }
    }

}