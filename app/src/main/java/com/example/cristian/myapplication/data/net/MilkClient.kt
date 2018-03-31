package com.example.cristian.myapplication.data.net

import com.example.cristian.myapplication.data.models.Produccion
import io.reactivex.Observable
import retrofit2.http.GET

interface MilkClient {

    @GET("/api/v1/milk")
    fun getMilkProduction():Observable<List<Produccion>>

}