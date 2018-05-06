package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Manage(var fecha: Date? = null,
             var bovino: String? = null,
             var numeroAplicaciones: Int? = null,
             var frecuencia: Int? = null,
             var tipo: String? = null,
             var otro: String? = null,
             var tratamiento: String? = null,
             var producto: String? = null,
             var observaciones: String? = null,
             var aplicaciones: List<Aplicacion>? = listOf()) : Parcelable