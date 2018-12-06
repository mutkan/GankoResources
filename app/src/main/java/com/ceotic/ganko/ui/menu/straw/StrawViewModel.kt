package com.ceotic.ganko.ui.menu.straw

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Straw
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.andEx
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
import io.reactivex.Single
import javax.inject.Inject

class StrawViewModel @Inject constructor (private val db: CouchRx,
                                          private val session:UserSession) : ViewModel() {

    fun addStraw(straw: Straw): Single<String> = checkId(straw.idStraw!!)
            .flatMap { if(it) db.insert(straw) else throw Throwable() }
            .applySchedulers()

    private fun checkId(id: String): Single<Boolean> =
            db.oneByExp("idStraw" equalEx id andEx  ("idFarm" equalEx session.farmID ), Straw::class)
                    .map { false }
                    .defaultIfEmpty(true)
                    .toSingle()
                    .applySchedulers()




}