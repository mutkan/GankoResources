package com.example.cristian.myapplication.ui.bovine.vaccination

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.data.models.Vacuna
import com.example.cristian.myapplication.util.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject

class VaccinationBvnViewModel @Inject constructor(private val db: CouchRx, private val bd: Database) : ViewModel() {


    fun listVaccineBovine(idBovino: String): Single<List<RegistroVacuna>> {
        val groupQuery = QueryBuilder.select(SelectResult.property("nombre"))
                .from(DataSource.database(bd))
                .where("bovines" containsEx idBovino andEx ("type" equalEx "Group"))
        return db.listByQuery(groupQuery)
                .flatMap { it.toObservable().map { it.getString(0) }.toList() }
                .flatMap { grupos ->
                    Log.d("GRUPOS", grupos.toString())
                    db.listByExp("bovinos" containsEx idBovino orEx ("grupo" inEx grupos), RegistroVacuna::class)
                }.applySchedulers()
    }


}