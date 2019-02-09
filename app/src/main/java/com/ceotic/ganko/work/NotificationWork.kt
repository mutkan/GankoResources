package com.ceotic.ganko.work

import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.work.*
import com.ceotic.ganko.App

import com.ceotic.ganko.R
import com.ceotic.ganko.ui.menu.MenuActivity
import java.util.concurrent.TimeUnit
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import com.ceotic.ganko.data.models.Alarm
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnActivity
import java.util.*
import kotlin.random.Random.Default.nextInt


class NotificationWork(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        val title = inputData.getString(ARG_TITLE) ?: "Ganko"
        val msg = inputData.getString(ARG_DESCRIPTION)
        val id = inputData.getString(ARG_ID)
        val type = inputData.getInt(ARG_TYPE, 0)

        val requestCode = nextInt()


        val icon = when (type) {
            TYPE_HEALTH -> R.drawable.ic_notify_hea
            TYPE_MANAGEMENT -> R.drawable.ic_notify_man
            TYPE_VACCINES -> R.drawable.ic_notify_vac
            TYPE_MEADOW -> R.drawable.ic_prairies
            else -> R.drawable.ic_bovine
        }
        /*val intent: Intent = when (type) {
            TYPE_HEALTH -> Intent(applicationContext, MenuActivity::class.java).apply { putExtra("fragment", 0) }
            TYPE_MANAGEMENT -> Intent(applicationContext, MenuActivity::class.java).apply { putExtra("fragment", 1) }
            TYPE_VACCINES -> Intent(applicationContext, MenuActivity::class.java).apply { putExtra("fragment", 2) }
            TYPE_MEADOW -> Intent(applicationContext, MenuActivity::class.java).apply { putExtra("fragment", 4) }
            else -> Intent(applicationContext, ReproductiveBvnActivity::class.java).apply { putExtra("idBovino", id) }
        }*/

        val intent = Intent(applicationContext, MenuActivity::class.java).apply { putExtra("fragment", 13) }

        val pendingIntent = PendingIntent.getActivity(applicationContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(applicationContext, App.CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(msg)
                .setVibrate(longArrayOf(500, 800, 500))
                .setLights(Color.GREEN, 3000, 3000)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setAutoCancel(true)
                .build()
        NotificationManagerCompat.from(applicationContext)
                .notify(requestCode, notification)

        return Result.success()
    }

    companion object {
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_ID = "ARG_ID"
        private const val ARG_DESCRIPTION = "ARG_DESCRIPTION"
        private const val ARG_TYPE = "ARG_TYPE"
        private const val ARG_REQUEST_CODE = "ARG_REQUEST_CODE"

        const val TYPE_HEALTH = 0
        const val TYPE_MANAGEMENT = 1
        const val TYPE_VACCINES = 2
        const val TYPE_REPRODUCTIVE = 3
        const val TYPE_MEADOW = 4
        const val TYPE_BOVINE = 5


        fun notify(type: Int, title: String, msg: String, docId: String, time: Long, timeUnit: TimeUnit): UUID {

            val data: Data = Data.Builder()
                    .putString(ARG_ID, docId)
                    .putString(ARG_TITLE, title)
                    .putString(ARG_DESCRIPTION, msg)
                    .putInt(ARG_TYPE, type)
                    .build()

            val notificationWork = OneTimeWorkRequestBuilder<NotificationWork>()
                    .setInitialDelay(time, timeUnit)
                    .setInputData(data)
                    .build()

            WorkManager.getInstance().enqueue(notificationWork)

            return notificationWork.id
        }

        fun cancelNotificationByTag(tag: String) {
            WorkManager.getInstance().cancelAllWorkByTag(tag)
        }

        fun cancelNotificationById(id: UUID) {
            WorkManager.getInstance().cancelWorkById(id)
        }

        fun cancelNotificationsById(vararg ids: UUID?) {
            ids.forEach { id ->
                id?.let { WorkManager.getInstance().cancelWorkById(it) }
            }
        }

        fun cancelAlarm(alarm: Alarm, device:Long){
            alarm.activa = false
            /*val idx = alarm.device.indexOfFirst { it.device == device }
            if(idx > -1){
               cancelNotificationById(UUID.fromString(alarm.device[idx].uuid))
            }*/
        }


    }

}