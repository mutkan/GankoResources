package com.example.cristian.myapplication.ui.menu.feed

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.models.RegistroAlimentacion
import com.example.cristian.myapplication.data.models.Straw
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject


class FeedViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession): ViewModel(){

    fun getFarmId():String = userSession.farmID
    fun getAllGroups(Idfinca: String): Observable<List<Group>> =
            db.listObsByExp("Idfinca" equalEx Idfinca, Group::class)
                    .applySchedulers()
    //Insertar alimentacion
    fun addFeed(feed: RegistroAlimentacion): Single<String> =
        db.insert(feed)
                .applySchedulers()

}