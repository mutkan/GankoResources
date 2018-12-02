package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.RegistroManejo
import com.ceotic.ganko.databinding.TemplateNextManageBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class NextManageAdapter @Inject constructor(): RecyclerView.Adapter<NextManageAdapter.NextManageHolder>() {

    val clickSkipManage: PublishSubject<RegistroManejo> = PublishSubject.create()

    val clickApplyManage: PublishSubject<RegistroManejo> = PublishSubject.create()

    var nextManages: List<RegistroManejo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextManageHolder
            = NextManageHolder(parent.inflate(R.layout.template_next_manage))

    override fun getItemCount(): Int = nextManages.size

    override fun onBindViewHolder(holder: NextManageHolder, position: Int) {
        holder.binding?.manage = nextManages[position]
        holder.binding?.isGroup = nextManages[position].grupo != null
        holder.binding?.clickSkipManage = clickSkipManage
        holder.binding?.clickApplyManage = clickApplyManage
    }


    class NextManageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding: TemplateNextManageBinding? = DataBindingUtil.bind(itemView)
    }


}