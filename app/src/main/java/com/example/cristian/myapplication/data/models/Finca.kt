package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Finca(
        var usuarioId: String,
        var nombre: String,
        var ubicacion: String,
        var hectareas: Int,
        var _id: String? = null,
        var _sequence: Long? = null,
        var type: String? = null) : Parcelable {
    init {
        type = javaClass.simpleName
    }
}