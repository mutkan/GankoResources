package com.ceotic.ganko.ui.bovine.reproductive

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.models.ProxStates.Companion.SKIPED
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.andEx
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.HashMap

class ReproductiveBvnViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    private val farmId = userSession.farmID

    fun getFarmID() = farmId

    fun getBovino(idBovino: String) = db.oneById(idBovino, Bovino::class).applySchedulers()

    fun getZeals(idBovino: String) = db.oneById(idBovino, Bovino::class)
            .map {bovino ->
                val zealsServedList = bovino.servicios?.filter { it.fechaUltimoCelo != null } ?: emptyList()
                val zealsServed = HashMap(zealsServedList.associateBy({it.fechaUltimoCelo!!},{true}))
                Triple(bovino.celos, bovino.fechaProximoCelo, zealsServed) }
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

    fun updateServicio(idBovino: String, servicio: Servicio,position:Int, failed:Boolean = false) = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                val servicios = b.servicios!!.toMutableList()
                servicios[position] = servicio
                b.servicios = servicios.toList()
                b.serviciosFallidos = if (failed) b.serviciosFallidos?.plus(1) ?: 1 else 0

                db.update(idBovino, b).map { b }
            }.applySchedulers()

    fun addParto(idBovino: String, servicio: Servicio,position: Int) = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                b.partos = b.partos?.plus(1) ?: 1
                val servicios = b.servicios!!.toMutableList()
                servicios[position] = servicio
                b.servicios = servicios.toList()
                db.update(idBovino, b).map { b to servicio }
            }.applySchedulers()

    fun updateBovino(idBovino: String,bovino: Bovino) = db.update(idBovino,bovino).applySchedulers()


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
            .map {servicios ->
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
            .map {servicios ->
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
                    .map {servicios ->
                        if (servicios.isNotEmpty()) {
                            val ultimoServicio = servicios.first()
                            val ultimoEvento = ultimoServicio.parto?.fecha ?: ultimoServicio.novedad!!.fecha
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
                s.add(0,servicio)
                bovino.servicios = s.toList()
                db.update(idBovino, bovino).map { bovino }
            }.applySchedulers()

    fun markStrawAsUsed(strawID: String): Single<Unit>
    = db.oneById(strawID,Straw::class).flatMapSingle { db.update(it._id!!,it.apply { state = Straw.USED_STRAW }) }.applySchedulers()

    fun getAllBulls(): Single<List<Bovino>> = db.listByExp("finca" equalEx farmId andEx ("genero" equalEx "Macho") andEx ("retirado" equalEx false), Bovino::class)
            .flatMap {
                it.toObservable().filter {
                    val dif = Date().time - it.fechaNacimiento!!.time
                    val dias = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                    val meses = dias / 30
                    meses >= 12
                }.toList()
            }

            .applySchedulers()

    fun getAllStraws(): Single<List<Straw>> = db.listByExp("idFarm" equalEx farmId andEx ("state" equalEx Straw.UNUSED_STRAW), Straw::class)
            .applySchedulers()

    fun insertNotifications(notifications:List<ReproductiveNotification>): Single<List<String>> = notifications.toObservable().flatMapSingle { db.insert(it) }.toList()

    fun markNotifcationsAsAppliedByTagAndBovineId(tag:String, bovineId:String): Single<List<Unit>> = db.listByExp("bovineId" equalEx bovineId andEx ("tag" equalEx tag),ReproductiveNotification::class)
            .flatMap {
                Log.d("NOTIFICATIONSS!!!!", it.size.toString())
                it.toObservable().flatMapSingle {reproductiveNotification ->
                    reproductiveNotification.estadoProximo = SKIPED
                    Log.d("CANCELING", "CANCELING")
                    db.update(reproductiveNotification._id!!, reproductiveNotification)
                }.toList()
            }


}