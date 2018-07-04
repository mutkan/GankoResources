package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Manage
import com.example.cristian.myapplication.data.models.RegistroManejo
import com.example.cristian.myapplication.databinding.TemplateRecentManageBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class RecentManageAdapter @Inject constructor(): RecyclerView.Adapter<RecentManageAdapter.RecentManageHolder>(){

    var recentManages: List<RegistroManejo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentManageHolder
            = RecentManageHolder(parent.inflate(R.layout.template_recent_manage))

    override fun getItemCount(): Int = recentManages.size

    override fun onBindViewHolder(holder: RecentManageHolder, position: Int) {
        holder.binding?.manage = recentManages[position]
        holder.binding?.isGroup = recentManages[position].grupo != null
    }

    class RecentManageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding: TemplateRecentManageBinding? = DataBindingUtil.bind(itemView)
    }

}