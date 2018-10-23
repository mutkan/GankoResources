package com.example.cristian.myapplication.ui.menu.straw

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Straw
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Single
import javax.inject.Inject

class StrawViewModel @Inject constructor (private val db: CouchRx) : ViewModel() {

    fun addStraw(straw: Straw): Single<String> = checkId(straw.idStraw!!)
            .flatMap { if(it) db.insert(straw) else throw Throwable() }
            .applySchedulers()

    private fun checkId(id: String): Single<Boolean> =
            db.oneByExp("idStraw" equalEx id, Straw::class)
                    .map { false }
                    .defaultIfEmpty(true)
                    .toSingle()
                    .applySchedulers()




}