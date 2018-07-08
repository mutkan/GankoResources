package com.example.cristian.myapplication.ui.bovine.vaccination

import android.arch.lifecycle.ViewModel
import com.couchbase.lite.Database
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.containsEx
import io.reactivex.Single
import javax.inject.Inject

class VaccinationBvnViewModel @Inject constructor(private val db: CouchRx, private val bd: Database) : ViewModel() {


    fun listVaccineBovine(idBovino: String): Single<List<RegistroVacuna>> {
        return db.listByExp("bovinos" containsEx idBovino, RegistroVacuna::class).applySchedulers()
    }


}