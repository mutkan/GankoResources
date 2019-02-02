package com.ceotic.ganko.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Alarm
import com.ceotic.ganko.databinding.TemplateItemNotificationBinding
import com.ceotic.ganko.util.inflate
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class NotificationsListAdapter @Inject constructor() : RecyclerView.Adapter<NotificationViewHolder>() {


    var data: List<Alarm> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var icons:IntArray = intArrayOf()
    val onClickNotification: PublishSubject<Alarm> = PublishSubject.create()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder
            = NotificationViewHolder(parent.inflate(R.layout.template_item_notification))

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(data[position], icons[data[position].alarma!!],onClickNotification)
    }

    override fun getItemCount(): Int = data.size

}

class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding: TemplateItemNotificationBinding = TemplateItemNotificationBinding.bind(itemView)
    fun bind(notification: Alarm, ic:Int, onClickNotification: PublishSubject<Alarm>) = binding.run {
        this.notification = notification
        this.onClickNotification = onClickNotification
        this.icon = ic
        this.bvnTxt = when{
            notification.grupo != null -> "Grupo ${notification.grupo!!.nombre}"
            notification.bovino != null -> "Codigo ${notification.bovino!!.codigo}"
            else -> "${notification.bovinos.size} Bovinos"
        }
    }

}