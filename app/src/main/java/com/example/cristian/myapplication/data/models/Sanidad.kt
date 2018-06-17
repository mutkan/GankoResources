package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Sanidad(
        var _id: String? = null,
        var _sequence: Long? = null,
        var type:String ? = null,
        var fecha: Date? = null,
        var fechaProx: Date? = null, //Calculo automatico
        var frecuencia: Int? = null,
        var evento: String? = null,
        var otra: String? = null,
        var diagnostico: String? = null,
        var tratamiento: String? = null,
        var producto: String? = null,
        var dosis: String? = null,
        var via: String? = null,
        var numeroAplicaciones: Float? = null,
        var aplicacion: Int? = null,
        var observaciones: String? = null,
        var valorProducto: Int? = null,
        var valorAtencion: Int? = null,
        var grupo: Grupo? = null,
        var bovinos: List<Bovinos> = listOf()

): Parcelable{

}

@Parcelize
class Grupo(
        var id: String? = null,
        var nombre: String? = null,
        var color: Int? = null
): Parcelable


@Parcelize
class Bovinos(
        var id: String? = null,
        var aplicacion: Boolean = true
):Parcelable