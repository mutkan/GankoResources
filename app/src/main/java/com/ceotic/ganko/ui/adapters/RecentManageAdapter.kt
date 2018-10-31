package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.RegistroManejo
import com.ceotic.ganko.databinding.TemplateRecentManageBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class RecentManageAdapter @Inject constructor(): RecyclerView.Adapter<RecentManageAdapter.RecentManageHolder>(){

    val clickManage: PublishSubject<RegistroManejo> = PublishSubject.create()

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
        holder.binding?.clickManage = clickManage
    }

    class RecentManageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding: TemplateRecentManageBinding? = DataBindingUtil.bind(itemView)
    }

}