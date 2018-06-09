package com.example.cristian.myapplication.ui.bovine.reproductive

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Servicio
import com.example.cristian.myapplication.data.models.Straw
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.andEx
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
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

    fun insertZeal(idBovino: String, zeal: Date, nextZeal: Date): Maybe<Unit> = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                Log.d("BOVINO", b.toString())
                val celos = b.celos?.toMutableList() ?: mutableListOf()
                b.fechaProximoCelo = nextZeal
                celos.add(0, zeal)
                b.celos = celos.toList()
                db.update(idBovino, b)
            }
            .applySchedulers()

    fun getServicesForBovine(idBovino: String): Maybe<List<Servicio>> = db.oneById(idBovino,Bovino::class).map { it.servicios }

    fun insertService(idBovino: String, servicio: Servicio): Single<Unit> = db.oneById(idBovino, Bovino::class)
            .flatMapSingle { bovino ->
                val s = bovino.servicios?.toMutableList() ?: mutableListOf()
                s.add(servicio)
                bovino.servicios = s.toList()
                db.update(idBovino, bovino)
            }.applySchedulers()

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

    fun getAllStraws(): Single<List<Straw>> = db.listByExp("idFarm" equalEx farmId, Straw::class)
            .applySchedulers()


}