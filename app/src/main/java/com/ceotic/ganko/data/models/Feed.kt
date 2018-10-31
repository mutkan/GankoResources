package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Feed(var _id: String? = null,
           var _sequence: Long? = null,
           var type: String? = null,
           var tipo: String? = null,
           var raza:String? =null,
           var bovino: String? = null,
           var fecha: Date? = null,
           var racion: Int? = null,
           var proposito: String?=null,
           var valorKilo: Int? = null,
           var otro: String? = null
) : Parcelable {
    init {
        type = javaClass.simpleName
    }

}