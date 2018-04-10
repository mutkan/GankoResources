package com.example.cristian.myapplication.ui.bovine.milk

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.data.net.MilkClient
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import com.example.cristian.myapplication.util.validateResponse
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class MilkBvnViewModel @Inject constructor(private val db:CouchRx):ViewModel(){

    fun getMilkProduction(idBovino:String):Single<List<Produccion>> =
            db.listByExp("idBovino" equalEx idBovino,Produccion::class)


    fun addMilkProduction(produccion:Produccion):Single<String> =
            db.insert(produccion)


}