package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Ceba
import com.example.cristian.myapplication.databinding.TemplateListCebaBovineBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ListCebaBovineAdapter @Inject constructor():RecyclerView.Adapter<ListCebaBovineAdapter.ListCebaBovineHolder>() {

    val onClickDeleteCeba:PublishSubject<Ceba> = PublishSubject.create()

    var data:List<Ceba> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCebaBovineHolder =
            ListCebaBovineHolder(parent.inflate(R.layout.template_list_ceba_bovine))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListCebaBovineHolder, position: Int) =
            holder!!.bind(data[position],onClickDeleteCeba)


    class ListCebaBovineHolder(listItem: View):RecyclerView.ViewHolder(listItem){
        val binding:TemplateListCebaBovineBinding = DataBindingUtil.bind(listItem)!!
        fun bind(ceba: Ceba,onClickDeleteCeba:PublishSubject<Ceba>){
            binding.ceba = ceba
            binding.onClickDeleteCeba = onClickDeleteCeba
        }
    }
}