package com.ceotic.ganko.ui.menu.feed

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Group
import com.ceotic.ganko.data.models.RegistroAlimentacion
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.equalEx
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