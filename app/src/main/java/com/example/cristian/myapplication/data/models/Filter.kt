package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Filter(var proposito_leche: Boolean= false,
             var proposito_ceba: Boolean = false,
             var proposito_Ambos: Boolean = false,
             var retirado: Boolean = false,
             var type: String? = null)
    : Parcelable {
    init {
        type = javaClass.simpleName
    }

}