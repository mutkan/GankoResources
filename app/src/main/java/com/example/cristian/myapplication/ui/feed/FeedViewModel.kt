package com.example.cristian.myapplication.ui.feed

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Observable
import javax.inject.Inject

class FeedViewModel @Inject constructor(private val db:CouchRx, private val userSession: UserSession):ViewModel(){

    fun getFarmId():String = userSession.farmID
    fun getAllGroups(Idfinca: String): Observable<List<Group>> =
            db.listByExp2("Idfinca" equalEx Idfinca, Group::class)
                    .applySchedulers()
}