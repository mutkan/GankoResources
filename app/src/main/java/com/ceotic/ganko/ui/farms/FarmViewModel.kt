package com.ceotic.ganko.ui.farms

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Alarm
import com.ceotic.ganko.data.models.Finca
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.andEx
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import com.ceotic.ganko.util.gt
import com.couchbase.lite.ArrayExpression
import com.couchbase.lite.Expression
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.util.*
import javax.inject.Inject

class FarmViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    private val userId = userSession.userId!!
    fun getUserId(): String = userId

    fun addFarm(farm: Finca): Single<String> = db.insert(farm).applySchedulers()
    fun deleteFarm(idFarm: String) = db.remove(idFarm)
            .flatMap {
                db.listByExp("idFinca" equalEx idFarm
                        andEx ("activa" equalEx false)
                        andEx ("fechaProxima" gt Date())
                        andEx ArrayExpression.any(ArrayExpression.variable("d")).`in`(Expression.property("device"))
                        .satisfies(ArrayExpression.variable("d.device").equalTo(Expression.longValue(userSession.device)))
                , Alarm::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMapSingle{
                            it.activa = false
                            db.update(it._id!!, it)
                        }
                        .toList()
            }
            .applySchedulers()

    fun updateFarm(idFarm: String, farm: Finca) = db.update(idFarm, farm).applySchedulers()
    fun getAllByUser(): Observable<List<Finca>> =
            db.listObsByExp("usuarioId" equalEx userId, Finca::class).applySchedulers()

    fun getFarmById(idFarm: String): Maybe<Finca> = db.oneById(idFarm, Finca::class).applySchedulers()
    fun setFarm(farmId: String, farm: String): Observable<Unit> = Observable.fromCallable {
        userSession.farmID = farmId
        userSession.farm = farm
    }.applySchedulers()

}