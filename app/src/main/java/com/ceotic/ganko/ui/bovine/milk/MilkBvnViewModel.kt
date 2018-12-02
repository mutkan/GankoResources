package com.ceotic.ganko.ui.bovine.milk

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.Produccion
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
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

fun getBovineById(idBovino: String) = db.oneById(idBovino, Bovino::class).applySchedulers()

}
