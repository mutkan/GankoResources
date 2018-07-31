package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Alarm
import com.example.cristian.myapplication.data.models.RegistroManejo
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.databinding.TemplateItemNotificationBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class NotificationsListAdapter @Inject constructor() : RecyclerView.Adapter<NotificationViewHolder>() {


    var data:List<Alarm> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder
            = NotificationViewHolder(parent.inflate(R.layout.template_item_notification))

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = data[position]
        when (notification.type){
            RegistroVacuna::class.java.simpleName ->{
                holder.bind(notification.titulo!!,"Vacuna",notification.descripcion!!)
            }
            RegistroManejo::class.java.simpleName ->{
                holder.bind(notification.titulo!!,"Manejo",notification.descripcion!!)
            }
            Sanidad::class.java.simpleName ->{
                holder.bind(notification.titulo!!,"Sanidad",notification.descripcion!!)
            }
        }

    }



    override fun getItemCount(): Int = data.size


}

class NotificationViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val binding:TemplateItemNotificationBinding = DataBindingUtil.bind(itemView)!!
    fun bind(titulo:String,tipo:String,descripcion:String) = binding.run {
        this.titulo = titulo
        this.tipo = tipo
        this.descripcion = descripcion
    }

}