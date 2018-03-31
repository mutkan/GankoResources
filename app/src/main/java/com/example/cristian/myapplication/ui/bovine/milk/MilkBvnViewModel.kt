package com.example.cristian.myapplication.ui.bovine.milk

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.data.net.MilkClient
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Observable
import javax.inject.Inject

class MilkBvnViewModel @Inject constructor(private val client : MilkClient):ViewModel(){

    fun getMilkProduction():Observable<List<Produccion>> =
            client.getMilkProduction()
                    .applySchedulers()

}