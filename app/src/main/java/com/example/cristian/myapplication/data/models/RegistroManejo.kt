package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class RegistroManejo (var _id: String? = null,
                      var sequence: Long? = null,
                      var type: String? = null,
                      var idFinca: String? = null,
                      var fecha: Date? = null,
                      var idDosisUno: String? = null, // = _id
                      var fechaProx: Date? = null,
                      var estadoProximaAplicacion: Int? = null,
                      var frecuencia: Int? = null,
                      var numeroAplicaciones: Int? = null,
                      var aplicacion: Int? = null,
                      var tipo: String? = null,
                      var otro: String? = null,
                      var tratamiento: String? = null,
                      var producto: String? = null,
                      var observaciones: String? = null,
                      var valorProducto: Int? = null,
                      var valorAsistencia: Int? = null,
                      var grupo: Group? = null,
                      var bovinos: List<String>? = listOf(),
                      var noBovinos: List<String>? = listOf()) : Parcelable {
    init {
        type = javaClass.simpleName
    }

    companion object {
        const val NOT_APPLIED = 0
        const val APPLIED = 1
        const val SKIPED = 2
    }

}