package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.databinding.TemplateSelectFeedBinding
import com.example.cristian.myapplication.databinding.TemplateSelectedBovinesBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class ListFeedSelectBovinesAdapter @Inject constructor(): RecyclerView.Adapter<ListFeedSelectBovinesAdapter.ListFeedSelectBovinesHolder>(){

    var feedSelectbovines: List<Bovino> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ListFeedSelectBovinesHolder, position: Int) {
        //holder!!.binding.feed = feed[position]
    }

    override fun getItemCount(): Int = feedSelectbovines.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFeedSelectBovinesHolder
            = ListFeedSelectBovinesHolder(parent.inflate(R.layout.template_select_feed))


    class ListFeedSelectBovinesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : TemplateSelectFeedBinding? = DataBindingUtil.bind(itemView)
    }
}
