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
                          var fechaProximaAplicacion: Date? = null,
                          var estadoProximaAplicacion: Int? = null,
                          var nombre: String? = null,
                          var dosisMl: Int? = null,
                          var frecuencia: Int? = null,
                          var unidadFrecuencia: String? = null,
                          var grupo: Grupo? = null,
                          var valor: Int? = null,
                          var bovinos: List<String>? = listOf(),
                          var noBovinos:List<String>? = listOf()
) : Parcelable {
    init {
        type = javaClass.simpleName
    }

    companion object {
        const val NOT_APPLIED = 0
        const val APPLIED = 1
        const val SKIPED = 2
    }
}