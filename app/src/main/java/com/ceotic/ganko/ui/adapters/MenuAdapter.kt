package com.ceotic.ganko.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.TemplateMenuBinding
import com.ceotic.ganko.databinding.TemplateMenuButtonBinding
import com.ceotic.ganko.databinding.TemplateMenuTitleBinding
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Ana Marin on 11/03/2018.
 */
class MenuAdapter @Inject constructor(): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val clickMenu = PublishSubject.create<Int>()
    var selected:Int = 0

    var items: List<MenuViewModel.MenuItem> = emptyList()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MenuTitleHolder -> {
                holder.binding!!.name = items[position].titleText
                holder.binding.root.tag = position
            }

            is MenuHolder -> {
                holder.binding!!.item = items[position]
                holder.binding.root.tag = position
                holder.binding.clickMenuItem = clickMenu
            }

            is MenuButtonHolder -> {
                holder.binding!!.item = items[position]
                holder.binding.root.tag = position
                holder.binding.clickMenuItem = clickMenu
            }
        }
    }

    override fun getItemViewType(position: Int): Int
            = items[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType){
        MenuViewModel.MenuItem.TYPE_MENU -> MenuHolder(parent.inflate(R.layout.template_menu))
        MenuViewModel.MenuItem.TYPE_TITLE -> MenuTitleHolder(parent.inflate(R.layout.template_menu_title))
        else ->  MenuButtonHolder(parent.inflate(R.layout.template_menu_button))
    }


    override fun getItemCount(): Int = items.size

    class MenuHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding: TemplateMenuBinding = DataBindingUtil.bind(itemView)!!
    }

    class MenuTitleHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding: TemplateMenuTitleBinding = DataBindingUtil.bind(itemView)!!
    }

    class MenuButtonHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding: TemplateMenuButtonBinding = DataBindingUtil.bind(itemView)!!
    }

    fun selectItem(position: Int, color:Int){
        items[selected].color = R.color.img
        items[position].color = color
        selected = position
        notifyDataSetChanged()
    }


}

