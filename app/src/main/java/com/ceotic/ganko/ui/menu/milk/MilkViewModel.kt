package com.ceotic.ganko.ui.menu.milk

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.SalidaLeche
import com.ceotic.ganko.util.applySchedulers
import io.reactivex.Single
import javax.inject.Inject

class MilkViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun addMilkProduction(leche: SalidaLeche): Single<String> =
            db.insert(leche)
                    .applySchedulers()

}