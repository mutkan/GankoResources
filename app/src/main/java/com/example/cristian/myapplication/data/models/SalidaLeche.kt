package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class SalidaLeche(
        var _id: String? = null,
        var _sequence: Long? = null,
        var type: String? = null,
        var idFarm: String? = null,
        var tipo: String? = null,
        var fecha: Date? = null,
        var operacion: String? = null,
        var valorLitro: Int? = null,
        var numeroLitros: Int? = null,
        var totalLitros: Int? = null): Parcelable{

    init {
        type = javaClass.simpleName
    }
}