package com.example.cristian.myapplication.ui.bovine.health

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.data.net.HealthClient
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class HealthBvnViewModel @Inject constructor(private val db: CouchRx):ViewModel(){

    fun getHealthBovine(idBovino: String): Single<List<Sanidad>> =
            db.listByExp("idBovino" equalEx idBovino, Sanidad::class)

}