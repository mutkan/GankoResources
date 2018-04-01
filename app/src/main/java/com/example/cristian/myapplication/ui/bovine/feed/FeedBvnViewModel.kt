package com.example.cristian.myapplication.ui.bovine.feed

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.net.FeedClient
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Observable
import javax.inject.Inject

class FeedBvnViewModel @Inject constructor(private val client : FeedClient):ViewModel(){

    fun getFeedById(idBovino: String): Observable<List<Feed>> =
            client.getFeedById(idBovino)
                    .applySchedulers()



}
