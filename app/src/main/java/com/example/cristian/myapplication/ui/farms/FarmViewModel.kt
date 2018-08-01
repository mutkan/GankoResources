package com.example.cristian.myapplication.ui.farms

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Finca
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class FarmViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    private val userId = userSession.userId
    fun getUserId(): String = userId!!

    fun addFarm(farm: Finca): Single<String> = db.insert(farm).applySchedulers()
    fun deleteFarm(idFarm: String) = db.remove(idFarm).applySchedulers()
    fun updateFarm(idFarm: String, farm: Finca) = db.update(idFarm, farm).applySchedulers()
    fun getAllByUser(): Observable<List<Finca>> =
            db.listObsByExp("usuarioId" equalEx userId!!, Finca::class).applySchedulers()
    fun getFarmById(idFarm: String): Maybe<Finca> = db.oneById(idFarm, Finca::class).applySchedulers()
    fun setFarm(farmId: String, farm: String): Observable<Unit> = Observable.fromCallable {
        userSession.farmID = farmId
        userSession.farm = farm
    }.applySchedulers()

}