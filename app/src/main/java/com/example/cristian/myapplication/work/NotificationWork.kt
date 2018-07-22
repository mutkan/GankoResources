package com.example.cristian.myapplication.work

import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.work.*
import com.example.cristian.myapplication.App

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.menu.MenuActivity
import java.util.concurrent.TimeUnit
import android.app.PendingIntent




class NotificationWork:Worker(){

    override fun doWork(): Result {

        val title = inputData.getString(ARG_TITLE, "Ganko")
        val msg = inputData.getString(ARG_DESCRIPTION, null)
        val id = inputData.getString(ARG_ID, null)
        val type = inputData.getInt(ARG_TYPE, 0)

        val icon = when(type){
            TYPE_HEALTH -> R.drawable.ic_notify_hea
            TYPE_MANAGEMENT -> R.drawable.ic_notify_man
            else -> R.drawable.ic_notify_vac
        }

        val intent =Intent(applicationContext, MenuActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(applicationContext, App.CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
        NotificationManagerCompat.from(applicationContext)
                .notify(102, notification)

        return Result.SUCCESS
    }

    companion object {
        private const val ARG_TITLE = ""
        private const val ARG_ID = ""
        private const val ARG_DESCRIPTION = ""
        private const val ARG_TYPE = ""

        const val TYPE_HEALTH = 0
        const val TYPE_MANAGEMENT = 1
        const val TYPE_VACCINES = 2

        fun notify(type:Int, title:String, msg:String, docId:String, time:Long, timeUnit: TimeUnit){

            val data: Data = mapOf(ARG_ID to docId,
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