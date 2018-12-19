package com.ceotic.ganko.ui.bovine.ceba

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.Ceba
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.andEx
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import com.couchbase.lite.Ordering
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject

class CebaViewModel @Inject constructor(private val db: CouchRx, private val session:UserSession) : ViewModel() {

    fun getListCeba(idBovino: String): Single<List<Ceba>> =
            db.listByExp("bovino" equalEx idBovino andEx ("eliminado" equalEx false),
                    orderBy = arrayOf(Ordering.property("fecha").descending()),
                    kClass = Ceba::class)
                    .applySchedulers()

    fun addCeba(ceba: Ceba): Single<String>{
        ceba.finca = session.farmID
        return db.insert(ceba)
                .applySchedulers()
    }

    fun lastCeba(idBovino:String):Maybe<Ceba> = db.listByExp("bovino" equalEx idBovino,limit = 1,
            orderBy =arrayOf(Ordering.property("fecha").descending()),
            kClass = Ceba::class)
            .flatMapMaybe { if(it.isNotEmpty()) Maybe.just(it[0]) else Maybe.empty() }

    fun getBovineInfo(idBovino: String): Maybe<Bovino> =
            db.oneById(idBovino,Bovino::class)
                    .applySchedulers()

    fun updateBovine(idBovino: String,bovino: Bovino):Single<Unit> =
            db.update(idBovino,bovino).applySchedulers()

    fun safeDeleteCeba(idCeba:String,ceba:Ceba):Single<Unit> =
            db.update(idCeba,ceba).applySchedulers()


}