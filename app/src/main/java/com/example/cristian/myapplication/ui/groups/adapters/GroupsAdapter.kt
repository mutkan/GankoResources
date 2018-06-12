package com.example.cristian.myapplication.ui.groups.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.databinding.TemplateGroupsBinding
import com.example.cristian.myapplication.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class GroupsAdapter @Inject constructor() : RecyclerView.Adapter<GroupsAdapter.GroupsHolder>() {

    var data: List<Group> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var editable: Boolean = false
    var onClick:PublishSubject<Group> = PublishSubject.create()
    var onDelete:PublishSubject<Group> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsHolder =
            GroupsHolder(parent.inflate(R.layout.template_groups))


    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: GroupsHolder, position: Int) {
        holder.bind(data[position], editable, onClick, onDelete)
    }


    class GroupsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: TemplateGroupsBinding = TemplateGroupsBinding.bind(itemView)

        fun bind(grp: Group, editState:Boolean, click:PublishSubject<Group>, delete:PublishSubject<Group>) = binding.run {
            grupo = grp
            editable = editState
            onClick = click
            onDelete = delete
        }
    }
}