package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Diagnostico(var _id: String? = null,
                  var _sequence: Long? = null,
                  var type: String? = null,
                  var fecha: Date? = null,
                  var novedad: String? = null) : Parcelable {
    init {
        type = javaClass.simpleName
    }

}