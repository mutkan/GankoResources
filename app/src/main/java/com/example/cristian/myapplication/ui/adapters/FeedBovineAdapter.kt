package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.models.RegistroAlimentacion
import com.example.cristian.myapplication.databinding.TemplateFeedBovineBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

/**
 * Created by Ana Marin on 20/03/2018.
 */
class FeedBovineAdapter @Inject constructor(): RecyclerView.Adapter<FeedBovineAdapter.FeedBovineHolder>(){

    var feed: List<RegistroAlimentacion> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: FeedBovineHolder, position: Int) {
        holder.binding?.feed = feed[position]
    }

    override fun getItemCount(): Int = feed.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedBovineHolder
            = FeedBovineHolder(parent.inflate(R.layout.template_feed_bovine))


    class FeedBovineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateFeedBovineBinding? = DataBindingUtil.bind(itemView)!!
    }
}