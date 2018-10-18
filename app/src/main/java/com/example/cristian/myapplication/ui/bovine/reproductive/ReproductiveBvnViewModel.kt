package com.example.cristian.myapplication.ui.bovine.reproductive

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.*
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.andEx
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import com.example.cristian.myapplication.util.toStringFormat
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReproductiveBvnViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    private val farmId = userSession.farmID

    fun getFarmID() = farmId

    fun getBovino(idBovino: String) = db.oneById(idBovino, Bovino::class).applySchedulers()

    fun getZeals(idBovino: String) = db.oneById(idBovino, Bovino::class)
            .map { Pair(it.celos, it.fechaProximoCelo) }
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

    fun updateServicio(idBovino: String, servicio: Servicio,position:Int) = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                val servicios = b.servicios!!.toMutableList()
                servicios[position] = servicio
                b.servicios = servicios.toList()
                db.update(idBovino, b).map { b }
            }.applySchedulers()

    fun addParto(idBovino: String, servicio: Servicio,position: Int) = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                b.partos = b.partos?.plus(1) ?: 1
                val servicios = b.servicios!!.toMutableList()
                servicios[position] = servicio
                b.servicios = servicios.toList()
                db.update(idBovino, b).map { b }
            }.applySchedulers()

    fun getOnServiceForBovine(idBovino: String): Single<List<Servicio>> = db.oneById(idBovino, Bovino::class).flatMapObservable { it.servicios?.toObservable() }
            .filter {
                it.finalizado!!.not()
            }.toList().applySchedulers()

    fun getServicesHistoryForBovine(idBovino: String): Single<List<Servicio>> = db.oneById(idBovino, Bovino::class).flatMapObservable { it.servicios?.toObservable() }
            .filter {
                it.finalizado!!
            }.toList().applySchedulers()

    fun getEmptyDaysForBovine(idBovino: String, servicioActual: Date): Single<Long> = db.oneById(idBovino, Bovino::class)
            .flatMapObservable {
                it.servicios?.toObservable()
            }.filter {
                it.parto != null
            }.toList()
            .map {
                if (it.isNotEmpty()) {
                    val ultimoParto = it.first().fecha!!.time
                    val dif = servicioActual.time - ultimoParto
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
                        it.parto != null
                    }.toList()
                    .map {
                        if (it.isNotEmpty()) {
                            val ultimoParto = it.first().parto!!.fecha
                            val dif = servicioActual.time - ultimoParto.time
                            val diasVacios = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                            return@map diasVacios to ultimoParto
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

    fun getAllBulls(): Single<List<Bovino>> = db.listByExp("finca" equalEx farmId andEx ("genero" equalEx "Macho"), Bovino::class)
            .flatMap {
                it.toObservable().filter {
                    val dif = Date().time - it.fechaNacimiento!!.time
                    val dias = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                    val meses = dias / 30
                    meses >= 18
                }.toList()
            }

            .applySchedulers()

    fun getAllStraws(): Single<List<Straw>> = db.listByExp("idFarm" equalEx farmId andEx ("state" equalEx Straw.UNUSED_STRAW), Straw::class)
            .applySchedulers()


}