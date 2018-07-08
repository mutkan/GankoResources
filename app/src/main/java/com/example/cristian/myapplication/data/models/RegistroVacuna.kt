package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class RegistroVacuna(var _id: String? = null,
                          var _sequence: Long? = null,
                          var type: String? = null,
                          var idFinca: String? = null,
                          var fecha: Date? = null,
                          var fechaProx: Date? = null,
                          var proxAplicado: Boolean? = null,
                          var nombre: String? = null,
                          var dosisMl: Int? = null,
                          var frecuenciaMeses: Int? = null,
                          var grupo: Grupo? = null,
                          var valor: Int? = null,
                          var bovinos: List<String>? = null
) : Parcelable {
    init {
        type = javaClass.simpleName
    }
}