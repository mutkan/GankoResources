package com.example.cristian.myapplication.data.net

import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.util.BodyResponse
import io.reactivex.Observable
import retrofit2.http.*

interface MilkClient {

    @GET("/api/v1/milk/{idBovino}")
    fun getMilkProduction(@Path ("idBovino")idBovno:String):Observable<BodyResponse<List<Produccion>>>

    @PUT("/api/v1/milk/{idBovino}")
    fun addMilkProduction(@Path("idBovino")idBovino:String,
                          @Body milkProduccion: Produccion):Observable<BodyResponse<Boolean>>

}