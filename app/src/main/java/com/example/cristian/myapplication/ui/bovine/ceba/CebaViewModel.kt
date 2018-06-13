package com.example.cristian.myapplication.ui.bovine.ceba

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Ceba
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject

class CebaViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun getListCeba(idBovino: String): Single<List<Ceba>> =
            db.listByExp("bovino" equalEx idBovino, Ceba::class)
                    .flatMapObservable { it.toObservable()  }
                    .filter { it.eliminado == false }
                    .toList()
                    .applySchedulers()

    fun addCeba(ceba: Ceba): Single<String> =
            db.insert(ceba)
                    .applySchedulers()
    fun getBovineInfo(idBovino: String): Maybe<Bovino> =
            db.oneById(idBovino,Bovino::class)
                    .applySchedulers()

    fun updateBovine(idBovino: String,bovino: Bovino):Single<Unit> =
            db.update(idBovino,bovino).applySchedulers()

    fun safeDeleteCeba(idCeba:String,ceba:Ceba):Single<Unit> =
            db.update(idCeba,ceba).applySchedulers()


}