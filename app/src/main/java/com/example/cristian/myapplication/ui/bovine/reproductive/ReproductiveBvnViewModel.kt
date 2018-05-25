package com.example.cristian.myapplication.ui.bovine.reproductive

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Diagnostico
import com.example.cristian.myapplication.data.models.Parto
import com.example.cristian.myapplication.data.models.Servicio
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.text.FieldPosition
import java.util.*
import javax.inject.Inject

class ReproductiveBvnViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

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

    fun insertService(idBovino: String, servicio: Servicio): Maybe<Unit> = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                Log.d("BOVINO", b.toString())
                val servicios = b.servicios?.toMutableList() ?: mutableListOf()
                servicios.add(0, servicio)
                b.servicios = servicios.toList()
                db.update(idBovino, b)
            }
            .applySchedulers()

    fun getServices(idBovino: String) = db.oneById(idBovino, Bovino::class)
            .map { it.servicios }
            .applySchedulers()

    fun insertDiagnosis(idBovino: String, servicio: Int, diagnostico: Diagnostico, confirmacion: Boolean): Maybe<Unit> = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                Log.d("BOVINO", b.toString())
                val servicios = b.servicios!!.toMutableList()
                val mServicio = servicios[servicio]
                val diagnosticos = mServicio.diagnosticos?.toMutableList() ?: mutableListOf()
                diagnosticos.add(0, diagnostico)
                mServicio.diagnosticos = diagnosticos.toList()
                mServicio.confirmacion = confirmacion
                servicios[servicio] = mServicio
                b.servicios = servicios.toList()
                db.update(idBovino, b)
            }
            .applySchedulers()

    fun insertBirth(idBovino: String, servicio: Int, parto: Parto): Maybe<Unit> = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                Log.d("BOVINO", b.toString())
                val servicios = b.servicios!!.toMutableList()
                val mServicio = servicios[servicio]
                mServicio.parto = parto
                servicios[servicio] = mServicio
                b.servicios = servicios.toList()
                db.update(idBovino, b)
            }
            .applySchedulers()


}