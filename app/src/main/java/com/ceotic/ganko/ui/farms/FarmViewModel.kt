package com.ceotic.ganko.ui.farms

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Finca
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class FarmViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    private val userId = userSession.userId!!
    fun getUserId(): String = userId

    fun addFarm(farm: Finca): Single<String> = db.insert(farm).applySchedulers()
    fun deleteFarm(idFarm: String) = db.remove(idFarm).applySchedulers()
    fun updateFarm(idFarm: String, farm: Finca) = db.update(idFarm, farm).applySchedulers()
    fun getAllByUser(): Observable<List<Finca>> =
            db.listObsByExp("usuarioId" equalEx userId, Finca::class).applySchedulers()
    fun getFarmById(idFarm: String): Maybe<Finca> = db.oneById(idFarm, Finca::class).applySchedulers()
    fun setFarm(farmId: String, farm: String): Observable<Unit> = Observable.fromCallable {
        userSession.farmID = farmId
        userSession.farm = farm
    }.applySchedulers()

}