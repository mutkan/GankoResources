package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Sanidad(
        var  fecha: Date,
        var evento: String,
        var otra: String?,
        var diagnostico: String,
        var tratamiento: String,
        var producto: String,
        var dosis: String,
        var via: String,
        var numeroAplicaciones: Int,
        var frecuencia: String,
        var observaciones: String,
        var aplicaciones: Array<Aplicacion>
): Parcelable {}