package com.example.cristian.myapplication.ui.menu.meadow

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class MeadowViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun updateMeadow(meadow:Pradera):Single<Unit> =
            db.update(meadow._id!!,meadow).applySchedulers()

    fun getMeadow(idMeadow:String):Maybe<Pradera> =
            db.oneById(idMeadow,Pradera::class).applySchedulers()

}