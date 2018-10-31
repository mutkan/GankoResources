package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Manage
import com.ceotic.ganko.databinding.TemplateManageBovineBinding
import com.ceotic.ganko.util.inflate
import javax.inject.Inject

/**
 * Created by Ana Marin on 20/03/2018.
 */
class ManageBovineAdapter @Inject constructor(): RecyclerView.Adapter<ManageBovineAdapter.ManageBovineHolder>(){

    var manage: List<Manage> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ManageBovineHolder, position: Int) {

    }

    override fun getItemCount(): Int = manage.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageBovineHolder
        = ManageBovineHolder(parent.inflate(R.layout.template_manage_bovine))


    class ManageBovineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateManageBovineBinding? = DataBindingUtil.bind(itemView)
    }
}