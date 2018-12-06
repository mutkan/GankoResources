package com.ceotic.ganko.data.models

import android.os.Parcelable
import android.util.Log
import com.ceotic.ganko.util.add
import com.ceotic.ganko.work.NotificationWork
import kotlinx.android.parcel.Parcelize
import java.util.*
import java.util.concurrent.TimeUnit

@Parcelize
class ReproductiveNotification(val _id: String? = null,
                               val _sequence: Long? = null,
                               var type: String? = null,
                               var uuid: UUID? = null,
                               var idFinca: String? = null,
                               var bovineId: String? = null,
                               val titulo: String? = null,
                               val descripcion: String? = null,
                               val fecha:Date? = null,
                               var fechaProxima: Date? = null,
                               var estadoProximo: Int? = null,
                               var tag:String? = null
) : Parcelable {
    init {
        type = javaClass.simpleName
    }

    companion object {

        fun setEmptyDaysNotifications(bovino:Bovino, fechaInicial:Date):List<ReproductiveNotification>{
            val dif = Date().time - fechaInicial.time
            val daysSinceEvent = TimeUnit.DAYS.convert((dif), TimeUnit.MILLISECONDS)
            Log.d("Dias desde evento", daysSinceEvent.toString())
            val notify45 = 44 - daysSinceEvent
            val notify60 = 59 - daysSinceEvent
            val notify90 = 89 - daysSinceEvent
            val notify120 = 119 - daysSinceEvent

            val title = "Recordatorio Días vacios"
            val notification45EmptyDays = createEmptyDaysNotification(bovino,notify45,title,fechaInicial,45)
            val notification60EmptyDays = createEmptyDaysNotification(bovino,notify60,title,fechaInicial,60)
            val notification90EmptyDays = createEmptyDaysNotification(bovino,notify90,title,fechaInicial,90)
            val notification120EmptyDays = createEmptyDaysNotification(bovino,notify120,title,fechaInicial,120)
            return listOf(notification45EmptyDays, notification60EmptyDays, notification90EmptyDays, notification120EmptyDays)
        }

        fun createEmptyDaysNotification(bovino:Bovino, notifyTime:Long, title:String, fechaInicial: Date, amount:Int): ReproductiveNotification {
            val msg = "El bovino ${bovino.nombre} cumplirá $amount días vacios mañana"
            val uuidEmptyDays = if (notifyTime >= 0) NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, title, msg, bovino._id!!, notifyTime, TimeUnit.DAYS) else null
            bovino.notificacionesReproductivo!!["$amount${Bovino.ALERT_EMPTY_DAYS}"] = uuidEmptyDays
            val daysToNotify = amount - 1
            val fechaNotificacion = fechaInicial.add(Calendar.DATE, daysToNotify)
            return ReproductiveNotification(
                    uuid = uuidEmptyDays,
                    bovineId = bovino._id,
                    idFinca = bovino.finca,
                    titulo = title,
                    descripcion = msg,
                    fecha = fechaNotificacion,
                    fechaProxima = fechaNotificacion,
                    estadoProximo = ProxStates.NOT_APPLIED,
                    tag = Bovino.ALERT_EMPTY_DAYS
            )
        }

    }


}

data class EmptyDaysValidations(
        var services: List<Servicio>,
        var activeServices:List<Servicio>,
        var confirmedFinishedServices:List<Servicio>,
        var emptyDays:Long
)