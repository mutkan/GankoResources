package com.ceotic.ganko.ui.bovine.reproductive

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.models.ProxStates.Companion.SKIPED
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.HashMap

class ReproductiveBvnViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun getFarmID() = userSession.farmID

    fun getBovino(idBovino: String) = db.oneById(idBovino, Bovino::class).applySchedulers()

    fun getZeals(idBovino: String) = db.oneById(idBovino, Bovino::class)
            .map { bovino ->
                val zealsServedList = bovino.servicios?.filter { it.fechaUltimoCelo != null }
                        ?: emptyList()
                val zealsServed = HashMap(zealsServedList.associateBy({ it.fechaUltimoCelo!! }, { true }))
                Triple(bovino.celos, bovino.fechaProximoCelo, zealsServed)
            }
            .applySchedulers()

    fun insertZeal(idBovino: String, zeal: Date, nextZeal: Date): Maybe<Bovino> = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                Log.d("BOVINO", b.toString())
                val celos = b.celos?.toMutableList() ?: mutableListOf()
                b.fechaProximoCelo = nextZeal
                celos.add(0, zeal)
                b.celos = celos.toList()
                db.update(idBovino, b).map { b }
            }
            .applySchedulers()

    fun updateServicio(idBovino: String, servicio: Servicio, position: Int, failed: Boolean = false) = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                val servicios = b.servicios!!.toMutableList()
                servicios[position] = servicio
                b.servicios = servicios.toList()
                b.serviciosFallidos = if (failed) (b.serviciosFallidos ?: 0) + 1 else 0

                db.update(idBovino, b).map { b }
            }.applySchedulers()

    fun addParto(idBovino: String, servicio: Servicio, position: Int) = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                b.partos = b.partos?.plus(1) ?: 1
                val servicios = b.servicios!!.toMutableList()
                servicios[position] = servicio
                b.servicios = servicios.toList()
                db.update(idBovino, b).map { b to servicio }
            }.applySchedulers()

    fun updateBovino(idBovino: String, bovino: Bovino) = db.update(idBovino, bovino).applySchedulers()


    fun getOnServiceForBovine(idBovino: String): Single<List<Servicio>> = db.oneById(idBovino, Bovino::class).flatMapObservable { it.servicios?.toObservable() }
            .filter {
                it.finalizado!!.not()
            }.toList().applySchedulers()

    fun getFinishedServicesForBovine(idBovino: String): Single<List<Servicio>> = db.oneById(idBovino, Bovino::class).flatMapObservable { it.servicios?.toObservable() }
            .filter {
                it.finalizado!! && it.diagnostico?.confirmacion == true
            }.toList().applySchedulers()

    fun getServicesHistoryForBovine(idBovino: String): Single<List<Servicio>> = db.oneById(idBovino, Bovino::class).flatMapObservable { it.servicios?.toObservable() }
            .filter {
                it.finalizado!!
            }.toList().applySchedulers()

    fun getEmptyDaysForBovine(idBovino: String, servicioActual: Date): Single<Long> = db.oneById(idBovino, Bovino::class)
            .flatMapObservable {
                it.servicios?.toObservable()
            }.filter {
                it.finalizado == true && it.diagnostico?.confirmacion == true
            }.toList()
            .map { servicios ->
                if (servicios.isNotEmpty()) {
                    val ultimoServicio = servicios.first()
                    val ultimoEvento = ultimoServicio.parto?.fecha ?: ultimoServicio.novedad!!.fecha
                    val dif = servicioActual.time - ultimoEvento.time
                    return@map TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                } else {
                    0L
                }
            }

    fun getEmptyDaysForBovine(idBovino: String): Single<Long> = db.oneById(idBovino, Bovino::class)
            .flatMapObservable {
                it.servicios?.toObservable()
            }.filter {
                it.finalizado == true && it.diagnostico?.confirmacion == true
            }.toList()
            .map { servicios ->
                if (servicios.isNotEmpty()) {
                    val ultimoServicio = servicios.first()
                    val ultimoEvento = ultimoServicio.parto?.fecha ?: ultimoServicio.novedad!!.fecha
                    val dif = Date().time - ultimoEvento.time
                    return@map TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                } else {
                    0L
                }
            }

    fun getLastBirthAndEmptyDays(idBovino: String, servicioActual: Date): Single<Pair<Long, Date?>> =
            db.oneById(idBovino, Bovino::class)
                    .flatMapObservable {
                        it.servicios?.toObservable()
                    }.filter {
                        it.finalizado == true && it.diagnostico?.confirmacion == true
                    }.toList()
                    .map { servicios ->
                        if (servicios.isNotEmpty()) {
                            val ultimoServicio = servicios.first()
                            val ultimoEvento = ultimoServicio.parto?.fecha
                                    ?: ultimoServicio.novedad!!.fecha
                            val ultimoServicioConParto = servicios.find { it.parto != null }
                            val fechaUltimoParto = ultimoServicioConParto?.parto?.fecha
                            val dif = servicioActual.time - ultimoEvento.time
                            val diasVacios = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                            return@map diasVacios to fechaUltimoParto
                        } else {
                            0L to null
                        }
                    }


    fun insertService(idBovino: String, servicio: Servicio): Single<Bovino> = db.oneById(idBovino, Bovino::class)
            .flatMapSingle { bovino ->
                val s = bovino.servicios?.toMutableList() ?: mutableListOf()
                s.add(0, servicio)
                bovino.servicios = s.toList()
                db.update(idBovino, bovino).map { bovino }
            }.applySchedulers()

    fun markStrawAsUsed(strawID: String): Single<Unit> = db.oneById(strawID, Straw::class).flatMapSingle { db.update(it._id!!, it.apply { state = Straw.USED_STRAW }) }.applySchedulers()

    fun getAllBulls(): Single<List<Bovino>> = db.listByExp("finca" equalEx userSession.farmID andEx ("genero" equalEx "Macho") andEx ("retirado" equalEx false), Bovino::class)
            .flatMap {
                it.toObservable().filter {
                    val dif = Date().time - it.fechaNacimiento!!.time
                    val dias = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                    val meses = dias / 30
                    meses >= 12
                }.toList()
            }

            .applySchedulers()

    fun getAllStraws(): Single<List<Straw>> = db.listByExp("idFarm" equalEx userSession.farmID andEx ("state" equalEx Straw.UNUSED_STRAW), Straw::class)
            .applySchedulers()

    // fun insertNotifications(notifications: List<ReproductiveNotification>): Single<List<String>> = notifications.toObservable().flatMapSingle { db.insert(it) }.toList()
    fun insertAlarm(alarm:Alarm, uuid:UUID) = db.insert(alarm.apply {
        idFinca = userSession.farmID
        device = listOf(AlarmDevice(userSession.device, uuid.toString()))
    })

    fun insertNotifications(notifications: List<Pair<Alarm, Long>>): Single<List<String>> = notifications.toObservable()
            .flatMapSingle { (alarm, time) ->
                val uuid = NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, alarm.titulo!!,
                        alarm.descripcion + ", Bovino ${alarm.bovino!!.codigo}", alarm.bovino!!.id,
                        time, TimeUnit.MINUTES, userSession.farmID)
                alarm.idFinca = userSession.farmID
                alarm.device = listOf(AlarmDevice(userSession.device, uuid.toString()))
                db.insert(alarm)
            }
            .toList()
            .applySchedulers()

    fun cancelNotiByDiagnosis(id: String, vararg types:Int , fromNow:Boolean = true):Single<List<Unit>>{

        val q = if(fromNow)
                ("reference" equalEx id
                andEx ("activa" equalEx true)
                andEx ("fechaProxima" gt Date())
                andEx ("alarma" inEx types.toList()))
        else
            ("reference" equalEx id
                    andEx ("activa" equalEx true)
                    andEx ("alarma" inEx types.toList()))

        return db.listByExp(q, Alarm::class)
                .flatMapObservable { it.toObservable() }
                .flatMapSingle {
                    it.activa = false
                    NotificationWork.cancelAlarm(it, userSession.device)
                    db.update(it._id!!, it)
                }
                .toList()
                .applySchedulers()

    }

    fun prepareNovelty(bovino: Bovino, src: Servicio): Single<List<Unit>> {
        val date = if(src.parto != null) src.parto!!.fecha!!.time/ 60000
        else src.novedad!!.fecha.time / 60000

        val now = Date().time / 60000

        return listOf((date + 64800) to "45", (date + 86400) to "60", (date + 129600) to "90", (date + 172800) to "120").toObservable()
                .map { (emptyDays, type)-> Triple(emptyDays, emptyDays- now, type)}
                .filter{it.second + DAY_7_MIN> 0}
                .map {(emptyDays, time, type)->
                    Alarm(
                            bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                            titulo = "$type Dias Vacios",
                            descripcion = "Se cumplen $type dias vacios",
                            alarma = when(type){
                                "45" -> ALARM_EMPTY_DAYS_45
                                "60" -> ALARM_EMPTY_DAYS_60
                                "90" -> ALARM_EMPTY_DAYS_90
                                else -> ALARM_EMPTY_DAYS_120
                            },
                            fechaProxima = Date(emptyDays * 60000),
                            type = TYPE_ALARM,
                            activa = true,
                            reference = bovino._id
                    ) to time
                }.toList()
                .flatMap(this::insertNotifications)
                .flatMap {cancelNotiByDiagnosis(bovino._id!!, ALARM_SECADO, ALARM_PREPARACION,
                        ALARM_NACIMIENTO, fromNow = false)  }
                .applySchedulers()

    }


    fun prepareBirthZeal(bovino: Bovino, birth:Date, title:String = "El Parto"): Single<List<String>> {
        val date = birth.time / 60000
        val now = Date().time / 60000

        return listOf((date + 34560) to "21", (date + 60480) to "42", (date + 92160) to "64", (date + 120960) to "84").toObservable()
                .map { (emptyDays, type)-> Triple(emptyDays, emptyDays- now, type)}
                .filter{it.second + DAY_7_MIN > 0}
                .map {(emptyDays, time, type)->
                    Alarm(
                            bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                            titulo = "$type Dias Desde $title",
                            descripcion = "Se cumplen $type dias desde ${title.toLowerCase()}, verificar celo",
                            alarma = when(type){
                                "21" -> ALARM_ZEAL_21
                                "42" -> ALARM_ZEAL_42
                                "64" -> ALARM_ZEAL_64
                                else -> ALARM_ZEAL_84
                            },
                            fechaProxima = Date(emptyDays * 60000),
                            type = TYPE_ALARM,
                            activa = true,
                            reference = bovino._id
                    ) to time
                }.toList()
                .flatMap(this::insertNotifications)
                .applySchedulers()
    }


}