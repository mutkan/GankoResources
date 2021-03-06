package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Ceba(var _id: String? = null,
           var _sequence:Long?=null,
           var type:String?=null,
           var finca: String? = null,
           var bovino: String? = null,
           var fecha: Date? = null,
           var peso: Float? = null,
           var gananciaPeso: Float? = null,
           var eliminado:Boolean? = null): Parcelable {
    init {
        type = javaClass.simpleName
    }

}