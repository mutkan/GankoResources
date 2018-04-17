package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Aplicacion(var fecha: Date? = null,
                 var aplicado: Boolean? = null,
                 var valorProducto: Int? = null,
                 var valorAsistencia: Int? = null) : Parcelable