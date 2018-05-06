package com.example.cristian.myapplication.ui.bovine

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Single
import javax.inject.Inject

class BovineViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun getFarmId() = userSession.farmID

    fun addBovine(bovino: Bovino): Single<String> =
            db.insert(bovino)
                    .applySchedulers()

}