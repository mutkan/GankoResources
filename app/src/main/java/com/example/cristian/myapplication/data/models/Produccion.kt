package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Produccion(
        var tipo: String,
        var bovino: String,
        var jornada: String,
        var litros: String,
        var fecha: Date
):Parcelable {}