package com.example.cristian.myapplication.ui.bovine.milk

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.data.net.MilkClient
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.validateResponse
import io.reactivex.Observable
import javax.inject.Inject

class MilkBvnViewModel @Inject constructor(private val client : MilkClient):ViewModel(){

    fun getMilkProduction(idBovino:String):Observable<List<Produccion>> =
            client.getMilkProduction(idBovino)
                    .flatMap { validateResponse(it) }
                    .applySchedulers()

    fun addMilkProduction(idBovino: String,produccion:Produccion):Observable<Boolean> =
            client.addMilkProduction(idBovino,produccion)
                    .flatMap { validateResponse(it) }
                    .applySchedulers()


}