package com.example.cristian.myapplication.ui.menu.health

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class HealthViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {
    private val farmID = userSession.farmID
    fun getFarmId(): String = farmID


    fun addHealth(health: Sanidad): Single<String> =
            db.insert(health)
                    .applySchedulers()
    fun getHealth(): Single<List<Sanidad>> = db.listByExp("idFinca" equalEx farmID, Sanidad::class)
            .applySchedulers()


}