package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Ceba(var _id: String? = null,
           var _sequence:Long?=null,
           var type:String?=null,
           var tipo: String? = null,
           var bovino: String? = null,
           var fecha: Date? = null,
           var peso: Float? = null,
           var gananciaPeso: Float? = null): Parcelable {
    init {
        type = javaClass.simpleName
    }

}