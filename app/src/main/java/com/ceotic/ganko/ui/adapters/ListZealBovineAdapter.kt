package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.TemplateZealBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class ListZealAdapter @Inject constructor() : RecyclerView.Adapter<ZealViewHolder>() {

    var zeals: List<Date> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var zealsServed:HashMap<Date,Boolean> = hashMapOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onService:Boolean = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    val clickAddService: PublishSubject<Date> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZealViewHolder = ZealViewHolder(parent.inflate(R.layout.template_zeal))

    override fun getItemCount(): Int = zeals.size

    override fun onBindViewHolder(holder: ZealViewHolder, position: Int) {
        val zeal = zeals[position]
        holder.binding.zeal = zeal
        holder.binding.clickAddService = clickAddService
        val isZealServed = zealsServed[zeal] ?: false
        holder.binding.btnAddService.visibility = if (!isZealServed && !onService) View.VISIBLE else View.GONE
    }
}

class ZealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: TemplateZealBinding = DataBindingUtil.bind(itemView)!!
}
