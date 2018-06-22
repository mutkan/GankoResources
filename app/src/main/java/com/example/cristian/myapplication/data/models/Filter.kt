package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Filter(var milk_purpose: Boolean= false,
             var meat_purpose: Boolean = false,
             var both_purpose: Boolean = false,
             var retired: Boolean = false,
             var produccion:Boolean = false,
             var ceba: Boolean = false,
             var servicio: Boolean = false,
             var diagnostico:Boolean = false,
             var destete:Boolean = false,
             var type: String? = null)
    : Parcelable {
    init {
        type = javaClass.simpleName
    }

}