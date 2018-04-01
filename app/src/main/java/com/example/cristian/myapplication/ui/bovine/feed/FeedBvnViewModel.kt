package com.example.cristian.myapplication.ui.bovine.feed

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.net.FeedClient
import com.example.cristian.myapplication.util.BodyResponse
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.validateResponse
import io.reactivex.Observable
import javax.inject.Inject

class FeedBvnViewModel @Inject constructor(private val client : FeedClient,
                                           private val idBovino: String):ViewModel(){

    fun getFeedById(): Observable<List<Feed>> =
            client.getFeedById(idBovino)
                    .applySchedulers()



}
