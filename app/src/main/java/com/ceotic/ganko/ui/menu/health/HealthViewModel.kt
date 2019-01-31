package com.ceotic.ganko.ui.menu.health

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_add_health.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HealthViewModel @Inject constructor(private val db: CouchRx,
                                          private val userSession: UserSession) : ViewModel() {

    private val farmID = userSession.farmID
    fun getFarmId(): String = farmID


    fun addFirstHealth(health: Sanidad, notifyTime:Long): Single<String> =
            db.insertDosisUno(health)
                    .flatMap { prepareBovine(it, health)}
                    .flatMap { prepareAlarm(it, health, notifyTime)}
                    .applySchedulers()


    fun addHealth(health: Sanidad): Single<String> =
            db.insert(health)
                    .applySchedulers()

    fun updateHealth(sanidad: Sanidad): Single<Unit>
            = db.update( sanidad._id!!,sanidad)
            .flatMap { cancelAlarm(sanidad) }
            .applySchedulers()

    private fun prepareBovine(id:String, health:Sanidad) = if(health.bovinos.size == 1 && health.grupo == null)
        db.oneById(health.bovinos[0], Bovino::class)
                .map { "${it._id};;${it.nombre};;${it.codigo}" to id }
                .defaultIfEmpty("" to id)
                .toSingle()
    else Single.just("" to id)

    private fun prepareAlarm(data:Pair<String, String>, health:Sanidad, notifyTime: Long):Single<String>{
        val bvn = data.first.split(";;")
        var bvnInfo:AlarmBovine? = null
        var info = ""
        if(bvn.isNotEmpty()){
            bvnInfo = AlarmBovine(bvn[0], bvn[1], bvn[2])
            info = "- Bovino ${bvnInfo.codigo}"
        }

        if(health.grupo != null){
            info = "- Grupo ${health.grupo!!.nombre}"
        }

        val title = "${if(health.evento == "Otra") health.otra else health.evento}- Aplicación ${(health.aplicacion?:0) + 1}"
        val description = health.tratamiento + info

        val uuid = NotificationWork.notify(NotificationWork.TYPE_HEALTH, title,
                description, data.second, notifyTime, TimeUnit.HOURS)

        val alarm = Alarm(
                titulo = title,
                descripcion = health.tratamiento,
                alarma = ALARM_HEALTH,
                idFinca = farmID,
                fechaProxima = health.fechaProxima,
                type = TYPE_ALARM,
                device = listOf(
                        AlarmDevice(userSession.device, uuid.toString())
                ),
                grupo = health.grupo,
                bovinos = health.bovinos,
                activa = true,
                reference = data.second,
                bovino = bvnInfo
        )

        return db.insert(alarm)
    }

    private fun cancelAlarm(health:Sanidad):Single<Unit>{
        return db.oneByExp("reference" equalEx  health._id!!
                andEx ("activa" equalEx false)
                andEx ("fechaProxima" gt Date())
                , Alarm::class )
                .flatMapSingle {
                    NotificationWork.cancelAlarm(it, userSession.device )
                    db.update(it._id!!, it)
                }
    }

}