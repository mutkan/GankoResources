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
                holder.bind(notification,"Vacuna")
            }
            RegistroManejo::class.java.simpleName ->{
                holder.bind(notification,"Manejo")
            }
            Sanidad::class.java.simpleName ->{
                holder.bind(notification,"Sanidad")
            }
        }

    }



    override fun getItemCount(): Int = data.size


}

class NotificationViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val binding:TemplateItemNotificationBinding = DataBindingUtil.bind(itemView)!!
    fun bind(notification:Alarm,tipo:String) = binding.run {
        this.notification = notification
        this.tipo = tipo
    }

}