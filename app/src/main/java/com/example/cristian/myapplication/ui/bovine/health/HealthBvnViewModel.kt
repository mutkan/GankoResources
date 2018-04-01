package com.example.cristian.myapplication.ui.bovine.health

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.data.net.HealthClient
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Observable
import javax.inject.Inject

class HealthBvnViewModel @Inject constructor(private val client: HealthClient):ViewModel(){

    fun getHealthBovine(): Observable<List<Sanidad>> =
            client.getHealthBovine()
                    .applySchedulers()

}