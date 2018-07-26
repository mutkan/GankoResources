package com.example.cristian.myapplication.ui.menu.meadow

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.MeadowAlarm
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class MeadowViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun updateMeadow(meadow:Pradera):Single<Unit> =
            db.update(meadow._id!!,meadow).applySchedulers()

    fun getMeadow(idMeadow:String):Maybe<Pradera> =
            db.oneById(idMeadow,Pradera::class).applySchedulers()

    fun addMeadowAlert(alarm: MeadowAlarm):Single<String> =
            db.insert(alarm).applySchedulers()

    fun getMeadowAlert(idMeadow: String): Observable<List<MeadowAlarm>> =
            db.listObsByExp("meadow" equalEx idMeadow,MeadowAlarm::class)
                    .applySchedulers()

    fun deleteMeadowAlert(idMeadowAlarm:String):Single<Unit> =
            db.remove(idMeadowAlarm).applySchedulers()
}