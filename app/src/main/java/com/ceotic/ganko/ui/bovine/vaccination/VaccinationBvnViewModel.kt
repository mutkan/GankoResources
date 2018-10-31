package com.ceotic.ganko.ui.bovine.vaccination

import android.arch.lifecycle.ViewModel
import com.couchbase.lite.Database
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.RegistroVacuna
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.containsEx
import io.reactivex.Single
import javax.inject.Inject

class VaccinationBvnViewModel @Inject constructor(private val db: CouchRx, private val bd: Database) : ViewModel() {


    fun listVaccineBovine(idBovino: String): Single<List<RegistroVacuna>> {
        return db.listByExp("bovinos" containsEx idBovino, RegistroVacuna::class).applySchedulers()
    }


}