package com.example.cristian.myapplication.ui.bovine.feed

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Single
import javax.inject.Inject

class FeedBvnViewModel @Inject constructor(private val db: CouchRx):ViewModel(){

    fun getFeedBovine(idBovino: String): Single<List<Feed>> =
            db.listByExp("bovino" equalEx idBovino, Feed::class)



}
