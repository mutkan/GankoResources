package com.example.cristian.myapplication.ui.menu.milk

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.data.models.SalidaLeche
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Single
import javax.inject.Inject

class MilkViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun addMilkProduction(leche: SalidaLeche): Single<String> =
            db.insert(leche)
                    .applySchedulers()

}