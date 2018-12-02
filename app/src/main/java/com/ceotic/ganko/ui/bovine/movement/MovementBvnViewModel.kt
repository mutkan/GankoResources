package com.ceotic.ganko.ui.bovine.movement

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Movimiento
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.*
import io.reactivex.Single
import javax.inject.Inject


class MovementBvnViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession):ViewModel(){

    fun getMovementBovine(idBovino: String): Single<List<Movimiento>> =
            db.listByExp(("bovinos" containsEx idBovino) andEx ("idFarm" equalEx userSession.farmID), Movimiento::class)
                    .applySchedulers()



}