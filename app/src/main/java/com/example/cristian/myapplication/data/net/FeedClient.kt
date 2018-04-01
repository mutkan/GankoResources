package com.example.cristian.myapplication.data.net

import com.example.cristian.myapplication.data.models.Feed
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface FeedClient {

    @GET("/api/v1/feed/get-by-id/{id}")
    fun getFeedById(@Path("id") idBovino: String): Observable<List<Feed>>
}