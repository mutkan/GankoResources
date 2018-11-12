package com.ceotic.ganko.data.net

import com.ceotic.ganko.data.models.LoginResponse
import com.ceotic.ganko.data.models.UserLogin
import com.ceotic.ganko.util.BodyResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClient {

    @POST("/api/v1/user/login")
    fun login(@Body userLogin: UserLogin): Observable<BodyResponse<LoginResponse>>
}