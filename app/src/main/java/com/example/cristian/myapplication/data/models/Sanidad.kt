package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Sanidad(var fecha: Date? = null,
              var bovino: String? = null,
              var evento: String? = null,
              var nombre: String? = null,
              var otra: String? = null,
              var diagnostico: String? = null,
              var tratamiento: String? = null,
              var producto: String? = null,
              var dosis: String? = null,
              var via: String? = null,
              var numeroAplicaciones: Int? = null,
              var frecuencia: String? = null,
              var observaciones: String? = null,
              var aplicaciones: List<Aplicacion>? = listOf()) : Parcelable