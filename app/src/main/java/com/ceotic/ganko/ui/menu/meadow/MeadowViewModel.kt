package com.ceotic.ganko.ui.menu.meadow

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Alarm
import com.ceotic.ganko.data.models.AlarmDevice
import com.ceotic.ganko.data.models.MeadowAlarm
import com.ceotic.ganko.data.models.Pradera
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import com.ceotic.ganko.work.NotificationWork
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MeadowViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun updateMeadow(meadow:Pradera):Single<Unit> =
            db.update(meadow._id!!,meadow).applySchedulers()

    fun getMeadow(idMeadow:String):Maybe<Pradera> =
            db.oneById(idMeadow,Pradera::class).applySchedulers()

    fun addMeadowAlert(alarm: MeadowAlarm):Single<String> =
            db.insert(alarm.apply { idFinca = userSession.farmID }).applySchedulers()

    fun getMeadowAlert(idMeadow: String): Observable<List<MeadowAlarm>> =
            db.listObsByExp("meadow" equalEx idMeadow,MeadowAlarm::class)
                    .applySchedulers()

    fun deleteMeadowAlert(idMeadowAlarm:String):Single<Unit> =
            db.remove(idMeadowAlarm).applySchedulers()

    fun insertAlarm(alarm: Alarm, timeUnit:Long):Single<String>{
        val uuid = NotificationWork.notify(NotificationWork.TYPE_MEADOW, alarm.titulo!!, alarm.descripcion!!,
                alarm.reference!!, timeUnit, TimeUnit.HOURS, userSession.farmID)
        alarm.idFinca = userSession.farmID
        alarm.device = listOf(AlarmDevice(userSession.device, uuid.toString()))

        return db.insert(alarm)
    }
}