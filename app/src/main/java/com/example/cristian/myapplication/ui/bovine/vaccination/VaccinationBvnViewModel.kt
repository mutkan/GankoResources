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
        return db.listByExp("bovinos" containsEx idBovino, RegistroVacuna::class).applySchedulers()
    }


}