package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Pradera
import com.ceotic.ganko.databinding.TemplateMeadowBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject

class MeadowAdapter : RecyclerView.Adapter<MeadowAdapter.MeadowHolder>() {

    val onClickMeadow: PublishSubject<Pradera> = PublishSubject.create()

    var data: MutableList<Pradera> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeadowHolder =
            MeadowHolder(parent.inflate(R.layout.template_meadow))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MeadowHolder, position: Int) =
            holder.bind(data[position], onClickMeadow)

    class MeadowHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: TemplateMeadowBinding = DataBindingUtil.bind(view)!!
        fun bind(pradera: Pradera, onClickMeadow: PublishSubject<Pradera>) {
            binding.pradera = pradera
            binding.onClickMeadow = onClickMeadow
        }
    }
}