package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.databinding.TemplateGroupsBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class  ListGroupsAdapter @Inject constructor():RecyclerView.Adapter<ListGroupsAdapter.ListGroupsHolder>(){

    var groups: List<Group> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListGroupsHolder
            = ListGroupsAdapter.ListGroupsHolder(parent.inflate(R.layout.template_groups))


    override fun getItemCount():Int = groups.size

    override fun onBindViewHolder(holder: ListGroupsHolder, position: Int) {}


    class ListGroupsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : TemplateGroupsBinding? = DataBindingUtil.bind(itemView)
    }
}