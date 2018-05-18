package com.example.cristian.myapplication.ui.bovine.reproductive

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class ReproductiveBvnViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun getZeals(idBovino: String) = db.oneById(idBovino,Bovino::class)
            .map { Pair(it.celos,it.fechaProximoCelo) }
            .applySchedulers()

    fun insertZeal(idBovino: String, zeal: Date, nextZeal:Date):Maybe<Unit> = db.oneById(idBovino, Bovino::class)
            .flatMapSingleElement { b ->
                Log.d("BOVINO", b.toString())
                val celos = b.celos?.toMutableList() ?: mutableListOf()
                b.fechaProximoCelo = nextZeal
                celos.add(0, zeal)
                b.celos = celos.toList()
                db.update(idBovino, b)
            }
            .applySchedulers()


}