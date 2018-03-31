package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Aplicacion(
        var fecha: Date,
        var aplicado: Boolean,
        var valorProducto: Int,
        var valorAsistencia: Int?
):Parcelable{}