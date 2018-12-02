package com.ceotic.ganko.ui.bovine.feed

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.RegistroAlimentacion
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.containsEx
import io.reactivex.Single
import javax.inject.Inject

class FeedBvnViewModel @Inject constructor(private val db: CouchRx):ViewModel(){

    fun getFeedBovine(idBovino: String): Single<List<RegistroAlimentacion>> =
            db.listByExp("bovinos" containsEx  idBovino, RegistroAlimentacion::class)
                    .applySchedulers()

}
