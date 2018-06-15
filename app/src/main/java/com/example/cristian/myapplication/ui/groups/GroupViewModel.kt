package com.example.cristian.myapplication.ui.groups

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.andEx
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class GroupViewModel @Inject constructor(private val db: CouchRx,
                                         private val session:UserSession): ViewModel(){

    private val pageSize = 30

    fun farmId():String = session.farmID

    fun list(): Single<List<Group>> =
            db.listByExp("finca" equalEx session.farmID, Group::class)
                    .applySchedulers()

    fun listObservable(): Observable<List<Group>> =
            db.listObsByExp("finca" equalEx session.farmID, Group::class)
                    .applySchedulers()

    fun add(group: Group):Single<Unit> = db.insert(group)
            .map { Unit }
            .applySchedulers()

    fun update(id:String, group: Group):Single<Unit> = db.update(id, group)
            .applySchedulers()

    fun remove(id:String):Single<Unit> = db.remove(id)
            .applySchedulers()

    fun listBovines(page:Int):Single<List<Bovino>>{
        val skip = page * pageSize
        return db.listByExp("finca" equalEx  session.farmID andEx ("retirado" equalEx false), Bovino::class, pageSize, skip)
                .applySchedulers()
    }

}

