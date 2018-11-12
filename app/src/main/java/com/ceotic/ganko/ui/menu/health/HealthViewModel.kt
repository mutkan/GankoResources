package com.ceotic.ganko.ui.menu.health

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Sanidad
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import io.reactivex.Single
import javax.inject.Inject

class HealthViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {
    private val farmID = userSession.farmID
    fun getFarmId(): String = farmID


    fun addFirstHealth(health: Sanidad): Single<String> =
            db.insertDosisUno(health)
                    .applySchedulers()
    fun addHealth(health: Sanidad): Single<String> =
            db.insert(health)
                    .applySchedulers()
    fun getHealth(): Single<List<Sanidad>> = db.listByExp("idFinca" equalEx farmID, Sanidad::class)
            .applySchedulers()

    fun updateHealth(sanidad: Sanidad): Single<Unit>
            = db.update( sanidad._id!!,sanidad).applySchedulers()

}