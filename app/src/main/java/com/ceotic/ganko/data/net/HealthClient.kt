package com.ceotic.ganko.data.net

import com.ceotic.ganko.data.models.Sanidad
import com.ceotic.ganko.util.BodyResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface HealthClient {

    @GET("/api/v1/get-sanidad-bovine/{idBovino}")
    fun getHealthById(@Path("idBovino") idBovino: String): Observable<BodyResponse<List<Sanidad>>>
}