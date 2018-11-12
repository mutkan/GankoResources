package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class RegistroVacuna(var _id: String? = null,
                          var _sequence: Long? = null,
                          var type: String? = null,
                          var idFinca: String? = null,
                          var idAplicacionUno:String? = null,
                          var titulo: String? = null,
                          var descripcion: String? = null,
                          var fecha: Date? = null,
                          var fechaProxima: Date? = null,
                          var estadoProximo: Int? = null,
                          var nombre: String? = null,
                          var dosisMl: Double? = null,
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

}