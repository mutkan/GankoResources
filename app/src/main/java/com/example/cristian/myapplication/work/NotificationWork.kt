package com.example.cristian.myapplication.work

import android.app.Notification
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.work.*
import com.example.cristian.myapplication.App

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.menu.MenuActivity
import java.util.concurrent.TimeUnit
import android.app.PendingIntent
import android.graphics.Color
import com.example.cristian.myapplication.ui.menu.MenuNavigation


class NotificationWork:Worker(){

    override fun doWork(): Result {

        val title = inputData.getString(ARG_TITLE, "Ganko")
        val msg = inputData.getString(ARG_DESCRIPTION, null)
        val id = inputData.getString(ARG_ID, null)
        val type = inputData.getInt(ARG_TYPE, 0)

        val icon = when(type){
            TYPE_HEALTH -> {R.drawable.ic_notify_hea}
            TYPE_MANAGEMENT -> {R.drawable.ic_notify_man}
            else -> {R.drawable.ic_notify_vac}
        }
        val intent: Intent = Intent( applicationContext, MenuActivity::class.java)
            when (type){
           TYPE_HEALTH-> intent.putExtra("fragment",0)
           TYPE_MANAGEMENT-> intent.putExtra("fragment",1)
           else -> intent.putExtra("fragment",2)
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(applicationContext, App.CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(msg)
                .setVibrate(longArrayOf(500,800,500))
                .setLights(Color.GREEN, 3000, 3000)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
        NotificationManagerCompat.from(applicationContext)
                .notify(102, notification)

        return Result.SUCCESS
    }

    companion object {
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_ID = "ARG_ID"
        private const val ARG_DESCRIPTION = "ARG_DESCRIPTION"
        private const val ARG_TYPE = "ARG_TYPE"

        const val TYPE_HEALTH = 0
        const val TYPE_MANAGEMENT = 1
        const val TYPE_VACCINES = 2

        fun notify(type:Int, title:String, msg:String, docId:String, time:Long, timeUnit: TimeUnit){

            val data: Data = mapOf( docId to ARG_ID,
                    ARG_TITLE to title,
                    ARG_DESCRIPTION to msg,
                    ARG_TYPE to type)
                    .toWorkData()

            val notificationWork = OneTimeWorkRequestBuilder<NotificationWork>()
                    .setInitialDelay(time, timeUnit)
                    .setInputData(data)
                    .build()

            WorkManager.getInstance().enqueue(notificationWork)
        }


    }

}