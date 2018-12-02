package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class MeadowAlarm (
    var _id: String? = null,
    var _sequence: Long? = null,
    var type: String? = null,
    var meadow:String? = null,
    var title: String? = null,
    var description: String? = null,
    var wasShowed: Boolean? = false,
    var fechaProxima: Date? = null,
    var tipo:String? = null
): Parcelable {
    init {
        type = javaClass.simpleName
    }
}