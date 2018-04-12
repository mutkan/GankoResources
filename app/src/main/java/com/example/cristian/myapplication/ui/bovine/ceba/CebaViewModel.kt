package com.example.cristian.myapplication.ui.bovine.ceba

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Ceba
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Single
import javax.inject.Inject

class CebaViewModel @Inject constructor(private val db:CouchRx):ViewModel(){

    fun getCeba(idBovino:String):Single<List<Ceba>> =
            db.listByExp("_id" equalEx idBovino,Ceba::class)

}