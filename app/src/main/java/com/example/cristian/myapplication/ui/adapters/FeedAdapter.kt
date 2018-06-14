package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.databinding.TemplateFeedBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class FeedAdapter @Inject constructor(): RecyclerView.Adapter<FeedAdapter.FeedHolder>(){

    var feed: List<Feed> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder
        = FeedHolder(parent.inflate(R.layout.template_feed))

    override fun getItemCount(): Int = feed.size

    override fun onBindViewHolder(holder: FeedHolder, position: Int) {
        //holder.binding?.feed = feed[position]
    }

    class FeedHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding: TemplateFeedBinding? = DataBindingUtil.bind(itemView)
    }

}