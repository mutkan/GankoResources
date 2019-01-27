package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Movimiento(
        var _id: String? = null,
        var _sequence: Long? = null,
        var type: String? = null,
        var idPradera: String? = null,
        var bovinos: List<String> = mutableListOf(),
        var transactionDate: Date,
        var idFarm: String,
        var fechaSalida: Date? = null,
        var diasLibres: Int = 0,
        var activo: Boolean? = false
) : Parcelable {
    init {
        type = javaClass.simpleName
    }
}