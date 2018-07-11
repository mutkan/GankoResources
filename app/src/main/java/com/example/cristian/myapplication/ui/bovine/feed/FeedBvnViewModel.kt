package com.example.cristian.myapplication.ui.bovine.feed

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.models.Finca
import com.example.cristian.myapplication.data.models.RegistroAlimentacion
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.containsEx
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class FeedBvnViewModel @Inject constructor(private val db: CouchRx):ViewModel(){

    fun getFeedBovine(idBovino: String): Single<List<RegistroAlimentacion>> =
            db.listByExp("bovinos" containsEx  idBovino, RegistroAlimentacion::class)
                    .applySchedulers()

}
