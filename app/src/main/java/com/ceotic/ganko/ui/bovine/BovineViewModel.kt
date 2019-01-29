package com.ceotic.ganko.ui.bovine

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Alarm
import com.ceotic.ganko.data.models.AlarmBovine
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.TYPE_ALARM
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.andEx
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import com.ceotic.ganko.work.NotificationWork
import io.reactivex.Single
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BovineViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun getFarmId() = userSession.farmID

    fun addBovine(bovino: Bovino): Single<String> = checkId(bovino.codigo!!)
            .flatMap { if (it) db.insert(bovino) else throw Throwable() }
            .flatMap { makeBirthAlarm(it, bovino, it) }
            .applySchedulers()

    fun addBovineWithImage(bovino: Bovino, field: String, file: File): Single<Pair<String, String>> = checkId(bovino.codigo!!)
            .flatMap { if (it) db.insert(bovino) else throw Throwable() }
            .flatMap { db.putBlob(it, field, "image/webp", file) }
            .flatMap { makeBirthAlarm(it.first, bovino, it) }
            .applySchedulers()

    private fun <T>makeBirthAlarm(id: String, bovino: Bovino, data:T):Single<T> = Single.create {
        bovino.fechaNacimiento?.let { birth->
            val timeMin = birth.time / 60_000
            val nowMin = Date().time / 60_000

            val twoMoths = (60 * 1_440) + timeMin
            makeNotification(id, nowMin, twoMoths, bovino, "Fiebre Aftosa")

            val treeMoths = (30 * 1_440) + twoMoths
            makeNotification(id, nowMin, treeMoths, bovino,
                    "Rabia",
                    "Carbono Sitomatico",
                    "Edema Maligno",
                    "Septicemia")

            val fourMoths = (30 * 1_440) + treeMoths
            makeNotification(id, nowMin, fourMoths, bovino, "Bruselocis")

            val twelveMoths = (240 * 1_440) + fourMoths
            makeNotification(id, nowMin, twelveMoths, bovino, "Carbon bactediriano")
        }

        it.onSuccess(data)
    }

    private fun makeNotification(id: String, from: Long, to: Long, bovine: Bovino, vararg vac:String) {
        if (from < to) {
            val alarm = Alarm(
                    bovino = AlarmBovine(id, bovine.nombre!!, bovine.codigo!!),
                    titulo = "Vacunas de Nacimiento",
                    descripcion = "Aplicar vacunas ${vac.joinToString(", ")}",
                    fechaProxima = Date(to),
                    type = TYPE_ALARM
            )
            db.insert(alarm)
            NotificationWork.notify(NotificationWork.TYPE_BOVINE, alarm.titulo!!, alarm.descripcion!!, id,
                    (to - from) + 5, TimeUnit.MINUTES)
        }
    }

    fun updateBovine(idBovine: String, bovine: Bovino) = db.update(idBovine, bovine).applySchedulers()

    fun getImage(idBovine: String, imageName: String) = db.getFile(idBovine, imageName).applySchedulers()

    private fun checkId(id: String): Single<Boolean> =
            db.oneByExp(("codigo" equalEx id) andEx ("finca" equalEx userSession.farmID), Bovino::class)
                    .map { false }
                    .defaultIfEmpty(true)
                    .toSingle()
                    .applySchedulers()

}
