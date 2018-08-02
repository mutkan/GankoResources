package com.example.cristian.myapplication.ui.bovine.movement

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.util.*
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject


class MovementBvnViewModel @Inject constructor(private val db: CouchRx, private val bd: Database):ViewModel(){

    fun getMovementBovine(idBovino: String): Single<List<Pradera>> =
            db.listByExp("bovinos" containsEx idBovino, Pradera::class)



}