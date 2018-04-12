package com.example.cristian.myapplication.data.net

import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.util.BodyResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface HealthClient {

    @GET("/api/v1/get-sanidad-bovine/{idBovino}")
    fun getHealthById(@Path("idBovino") idBovino: String): Observable<BodyResponse<List<Sanidad>>>
}