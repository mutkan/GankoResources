package com.example.cristian.myapplication.ui.bovine.health

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.util.*
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject

class HealthBvnViewModel @Inject constructor(private val db: CouchRx, private val bd: Database):ViewModel(){


    fun getHealthBovine(idBovino: String): Single<List<Sanidad>> =
            db.listByExp("bovinos" containsEx idBovino, Sanidad::class)

    fun listHealthBovine(idBovino: String): Single<List<Sanidad>> {
        val groupQuery = QueryBuilder.select(SelectResult.property("nombre"))
                .from(DataSource.database(bd))
                .where("bovines" containsEx idBovino andEx ("type" equalEx "Group"))
        return db.listByQuery(groupQuery)
                .flatMap { it.toObservable().map { it.getString(0) }.toList() }
                .flatMap { grupos ->
                    Log.d("GRUPOS", grupos.toString())
                    db.listByExp("bovinos" containsEx idBovino orEx  ("grupo" inEx grupos), Sanidad::class)
                }.applySchedulers()
    }
}