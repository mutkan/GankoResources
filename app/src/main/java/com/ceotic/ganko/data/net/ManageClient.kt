package com.ceotic.ganko.data.net

import com.ceotic.ganko.data.models.Manage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ManageClient{

    @GET("/api/v1/get-manage-bovine/{idBovino}")
    fun getManageById(@Path("id") idBovino: String): Observable<List<Manage>>
}