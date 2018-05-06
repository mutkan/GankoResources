package com.example.cristian.myapplication.ui.bovine.vaccination

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Vacuna
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject

class VaccinationBvnViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun listVaccineBovine(idBovino: String): Maybe<List<Vacuna>> =
            db.oneByExp("idBovino" equalEx idBovino, Bovino::class)
                    .map { it.vacunas }



}