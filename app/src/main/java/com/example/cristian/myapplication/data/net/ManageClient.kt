package com.example.cristian.myapplication.data.net

import com.example.cristian.myapplication.data.models.Manage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ManageClient{

    @GET("/api/v1/get-manage-bovine/{idBovino}")
    fun getManageById(@Path("id") idBovino: String): Observable<List<Manage>>
}