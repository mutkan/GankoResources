package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Produccion(var _id: String? = null,
                 var _sequence: Long? = null,
                 var type: String? = null,
                 var bovino: String? = null,
                 var jornada: String? = null,
                 var litros: String? = null,
                 var fecha: Date? = null) : Parcelable {
    init {
        type = javaClass.simpleName
    }

}