package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.databinding.TemplatePendingHealthBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class PendingHealthAdapter @Inject constructor():RecyclerView.Adapter<PendingHealthAdapter.PendingHealthHolder>(){

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
    }


    class PendingHealthHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var binding: TemplatePendingHealthBinding? = DataBindingUtil.bind(itemView)
    }
}