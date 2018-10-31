package com.ceotic.ganko.ui.bovine

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import io.reactivex.Single
import java.io.File
import javax.inject.Inject

class BovineViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun getFarmId() = userSession.farmID

    fun addBovine(bovino: Bovino): Single<String> = checkId(bovino.codigo!!)
            .flatMap { if (it) db.insert(bovino) else throw Throwable() }
            .applySchedulers()

    fun addBovineWithImage(bovino: Bovino, field: String, file: File): Single<Pair<String,String>> = checkId(bovino.codigo!!)
            .flatMap { if (it) db.insert(bovino) else throw Throwable() }
            .flatMap { db.putBlob(it, field, "image/webp", file) }
            .applySchedulers()

    fun updateBovine(idBovine: String, bovine: Bovino) = db.update(idBovine, bovine).applySchedulers()

    fun getImage(idBovine: String, imageName:String) = db.getFile(idBovine,imageName).applySchedulers()

    private fun checkId(id: String): Single<Boolean> =
            db.oneByExp("codigo" equalEx id, Bovino::class)
                    .map { false }
                    .defaultIfEmpty(true)
                    .toSingle()
                    .applySchedulers()

}
