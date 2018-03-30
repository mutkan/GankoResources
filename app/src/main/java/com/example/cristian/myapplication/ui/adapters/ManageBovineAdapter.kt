package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Manage
import com.example.cristian.myapplication.databinding.TemplateManageBovineBinding
import com.example.cristian.myapplication.util.inflate

/**
 * Created by Ana Marin on 20/03/2018.
 */
class ManageBovineAdapter : RecyclerView.Adapter<ManageBovineAdapter.ManageBovineHolder>(){

    var manage: List<Manage> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ManageBovineHolder?, position: Int) {

    }

    override fun getItemCount(): Int = manage.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ManageBovineHolder
        = ManageBovineHolder(parent!!.inflate(R.layout.template_manage_bovine))


    class ManageBovineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TemplateManageBovineBinding? = DataBindingUtil.bind(itemView)
    }
}