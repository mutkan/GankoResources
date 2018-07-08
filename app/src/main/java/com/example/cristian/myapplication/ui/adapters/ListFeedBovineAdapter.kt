package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.models.RegistroAlimentacion
import com.example.cristian.myapplication.databinding.TemplateFeedBinding
import com.example.cristian.myapplication.databinding.TemplateFeedBovineBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject
/**
 * Created by Ana Marin on 20/03/2018.
 */
class ListFeedBovineAdapter @Inject constructor(): RecyclerView.Adapter<ListFeedBovineAdapter.ListFeedBovineHolder>(){

    var feed: List<RegistroAlimentacion> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ListFeedBovineHolder, position: Int) {
        holder.binding.feeding = feed[position]
    }

    override fun getItemCount(): Int = feed.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFeedBovineHolder
            = ListFeedBovineHolder(parent.inflate(R.layout.template_feed))


    class ListFeedBovineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateFeedBinding = DataBindingUtil.bind(itemView)!!
    }
}
