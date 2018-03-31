package com.example.cristian.myapplication.data.net

import com.example.cristian.myapplication.data.models.Sanidad
import io.reactivex.Observable
import retrofit2.http.GET

interface HealthClient {

    @GET("/api/v1/get-sanidad-bovine/{idBovino}")
    fun getHealthBovine(): Observable<List<Sanidad>>
}