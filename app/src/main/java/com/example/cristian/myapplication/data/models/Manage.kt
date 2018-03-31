package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Manage(
        var fecha: Date,
        var numeroAplicaciones: Int,
        var frecuencia: Int,
        var tipo: String,
        var otro: String?,
        var tratamiento: String,
        var producto: String,
        var observaciones: String,
        var aplicaciones: Array<Aplicacion>
):Parcelable{}