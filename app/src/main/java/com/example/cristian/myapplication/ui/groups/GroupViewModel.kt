package com.example.cristian.myapplication.ui.groups

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class GroupViewModel @Inject constructor(private val db: CouchRx,
                                         private val session:UserSession): ViewModel(){

    private val pageSize = 30

    fun list(): Single<List<Group>> =
            db.listByExp("idFinca" equalEx session.farmID, Group::class)

    fun listObservable(): Observable<List<Group>> =
            db.listObsByExp("idFinca" equalEx session.farmID, Group::class)

    fun add(group: Group):Single<String> = db.insert(group)

    fun remove(id:String):Single<Unit> = db.remove(id)

    fun listBovines(page:Int):Single<List<Bovino>>{
        val skip = page * pageSize
        return db.listByExp("idfinca" equalEx  session.farmID, Bovino::class, pageSize, skip)
    }

}

