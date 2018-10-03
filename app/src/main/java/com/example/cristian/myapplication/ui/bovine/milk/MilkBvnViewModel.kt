package com.example.cristian.myapplication.ui.bovine.milk

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class MilkBvnViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    private val idFinca = userSession.farmID

    fun getMilkProduction(idBovino: String): Single<List<Produccion>> =
            db.listByExp("bovino" equalEx idBovino, Produccion::class)
                    .applySchedulers()

    fun getOne(idBovino: String): Maybe<Produccion> =
            db.oneById(idBovino, Produccion::class)
                    .applySchedulers()

    fun addMilkProduction(produccion: Produccion): Single<String> {
        produccion.idFinca = idFinca
        return db.insert(produccion)
                .applySchedulers()

    }


}

