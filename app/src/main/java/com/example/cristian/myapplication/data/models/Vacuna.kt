package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class Vacuna(
        var nombre: String,
        var dosis: Int,
        var aplicaciones: List<Aplicacion>,
        var valor:Int
):Parcelable{}