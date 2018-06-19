package com.example.cristian.myapplication.ui.menu.health

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Single
import javax.inject.Inject

class HealthViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun addHealthProduction(health: Sanidad): Single<String> =
            db.insert(health)
                    .applySchedulers()

}