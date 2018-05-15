package com.example.cristian.myapplication.ui.bovine.reproductive

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class ReproductiveBvnViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun insetZeal(idBovino: String, zeal: Date) = db.oneById("", Bovino::class)
            .flatMapObservable { b ->
                Observable.fromCallable {
                    val celos = b.celos?.toMutableList() ?: mutableListOf()
                    celos.add(zeal)
                    b.celos = celos.toList()
                    return@fromCallable b
                }.map {
                    db.update(idBovino, b)
                }

            }
}