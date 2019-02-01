package com.ceotic.ganko.ui.bovine

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
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

            val now = Calendar.getInstance()

            val nowMin = now.timeInMillis / 60_000
            val mins = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE)
            val timeMin = (birth.time / 60_000) + mins

            val twoMoths = (60 * 1_440) + timeMin + 2
            makeVaccineAlarm(id, nowMin, twoMoths, bovino, ALARM_2_MONTHS, "Fiebre Aftosa")

            val treeMoths = (30 * 1_440) + twoMoths + 2
            makeVaccineAlarm(id, nowMin, treeMoths, bovino, ALARM_3_MONTHS,
                    "Rabia",
                    "Carbono Sitomatico",
                    "Edema Maligno",
                    "Septicemia")

            val fourMoths = (30 * 1_440) + treeMoths + 2
            makeVaccineAlarm(id, nowMin, fourMoths, bovino, ALARM_4_MONTHS,  "Bruselocis")

            val twelveMoths = (240 * 1_440) + fourMoths + 2
            makeVaccineAlarm(id, nowMin, twelveMoths, bovino, ALARM_12_MONTHS,"Carbon bactediriano")

            makeReproductiveAlarm(id, nowMin, timeMin,  bovino)
        }

        it.onSuccess(data)
    }

    private fun makeReproductiveAlarm(id:String, nowMin:Long, birthMin:Long, bovine:Bovino){
        if(bovine.genero == "Hembra"){
            val to = birthMin + 777600
            if(nowMin < to){
                val title = "Inicio Reproductivo - ${bovine.codigo}"
                val description = "Bovino COD ${bovine.codigo}, Confirmación de ciclo reproductivo"
                makeNotification(id, title, description, bovine, to, to - nowMin, ALARM_18_MONTHS)
            }
        }
    }

    private fun makeVaccineAlarm(id: String, from: Long, to: Long, bovine: Bovino, alarm:Int, vararg vac:String) {
        if (from < to) {
            val title = "Vacunas de Nacimiento - ${bovine.codigo}"
            val description = "Bovino COD ${bovine.codigo}, Aplicar vacunas ${vac.joinToString(", ")}"
            makeNotification(id, title, description, bovine, to, to - from, alarm)

        }
    }

    private fun makeNotification(id:String, title:String, description:String, bovine:Bovino, to:Long, time:Long, alarmType:Int){
        val uuid = NotificationWork.notify(NotificationWork.TYPE_BOVINE, title, description, id,
                time, TimeUnit.MINUTES)
        val date = Date(to*60000)
        val alarm = Alarm(
                bovino = AlarmBovine(id, bovine.nombre!!, bovine.codigo!!),
                titulo = title,
                descripcion = description,
                alarma = alarmType,
                idFinca = userSession.farmID,
                fechaProxima = date,
                type = TYPE_ALARM,
                device = listOf(
                        AlarmDevice(userSession.device, uuid.toString())
                ),
                activa = true,
                reference = id
        )
        db.insertBlock(alarm)
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
