package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Feed
import com.ceotic.ganko.databinding.TemplateSelectedBovinesBinding
import com.ceotic.ganko.util.inflate
import javax.inject.Inject

/**
 * Created by Ana Marin on 20/03/2018.
 */
class ListFeedSelectedAdapter @Inject constructor(): RecyclerView.Adapter<ListFeedSelectedAdapter.ListFeedSelectedHolder>(){

    var feedSelected: List<Feed> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ListFeedSelectedHolder, position: Int) {
        //holder!!.binding.feed = feed[position]
    }

    override fun getItemCount(): Int = feedSelected.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFeedSelectedHolder
            = ListFeedSelectedHolder(parent.inflate(R.layout.activity_list_selected_bovines))


    class ListFeedSelectedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
          val binding : TemplateSelectedBovinesBinding? = DataBindingUtil.bind(itemView)
    }
}
