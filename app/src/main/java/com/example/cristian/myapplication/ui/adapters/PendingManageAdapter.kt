package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.RegistroManejo
import com.example.cristian.myapplication.databinding.TemplateNextManageBinding
import com.example.cristian.myapplication.databinding.TemplatePendingManageBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class PendingManageAdapter @Inject constructor(): RecyclerView.Adapter<PendingManageAdapter.PendingManageHolder>() {

    val clickSkipManage: PublishSubject<RegistroManejo> = PublishSubject.create()

    val clickApplyManage: PublishSubject<RegistroManejo> = PublishSubject.create()

    var pendingManages: List<RegistroManejo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingManageHolder
            = PendingManageHolder(parent.inflate(R.layout.template_pending_manage))

    override fun getItemCount(): Int = pendingManages.size

    override fun onBindViewHolder(holder: PendingManageHolder, position: Int) {
        holder.binding?.manage = pendingManages[position]
        holder.binding?.isGroup = pendingManages[position].grupo != null
        holder.binding?.clickSkipManage = clickSkipManage
        holder.binding?.clickApplyManage = clickApplyManage
    }


    class PendingManageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding: TemplatePendingManageBinding? = DataBindingUtil.bind(itemView)
    }

}