package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Vacuna(
        var nombre: String? = null,
        var dosis: Int? = null,
        var aplicaciones: List<Aplicacion>? = listOf(),
        var valor:Int? = null
):Parcelable