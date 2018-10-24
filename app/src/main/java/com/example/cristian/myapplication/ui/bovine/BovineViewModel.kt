package com.example.cristian.myapplication.ui.bovine

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Single
import javax.inject.Inject

class BovineViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun getFarmId() = userSession.farmID

    fun addBovine(bovino: Bovino): Single<String> = checkId(bovino.codigo!!)
            .flatMap { if(it) db.insert(bovino) else throw Throwable() }
                    .applySchedulers()

    fun updateBovine(idBovine: String, bovine:Bovino ) = db.update(idBovine, bovine).applySchedulers()

    private fun checkId(id: String): Single<Boolean> =
            db.oneByExp("codigo" equalEx id, Bovino::class)
                    .map { false }
                    .defaultIfEmpty(true)
                    .toSingle()
                    .applySchedulers()

}
