package com.example.cristian.myapplication.data.net

import com.example.cristian.myapplication.data.models.LoginResponse
import com.example.cristian.myapplication.data.models.Usuario
import com.example.cristian.myapplication.util.BodyResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClient {

    @POST("/api/v1/user/login")
    fun login(@Body userLogin: Usuario): Observable<BodyResponse<LoginResponse>>
}