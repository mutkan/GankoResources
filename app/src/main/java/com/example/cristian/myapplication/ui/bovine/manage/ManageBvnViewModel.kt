package com.example.cristian.myapplication.ui.bovine.manage

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.models.Manage
import com.example.cristian.myapplication.data.net.ManageClient
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class ManageBvnViewModel @Inject constructor(private val db: CouchRx):ViewModel(){

    fun getManageBovine(idBovino:String): Single<List<Manage>> =
           db.listByExp("bovino" equalEx idBovino, Manage::class)

}
