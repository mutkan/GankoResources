package com.example.cristian.myapplication.data.net

import com.example.cristian.myapplication.data.models.Feed
import io.reactivex.Observable
import retrofit2.http.GET

interface FeedClient {

    @GET("/api/v1/get-feed-bovine/{idBovino}")
    fun getFeedBovine(): Observable<List<Feed>>
}