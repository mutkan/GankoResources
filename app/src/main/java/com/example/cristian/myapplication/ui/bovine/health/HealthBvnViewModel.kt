package com.example.cristian.myapplication.ui.bovine.health

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.data.net.HealthClient
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.validateResponse
import io.reactivex.Observable
import javax.inject.Inject

class HealthBvnViewModel @Inject constructor(private val client: HealthClient):ViewModel(){

    fun getHealthBovine(idBovino: String): Observable<List<Sanidad>> =
            client.getHealthById(idBovino)
                    .flatMap { validateResponse(it) }
                    .applySchedulers()

}