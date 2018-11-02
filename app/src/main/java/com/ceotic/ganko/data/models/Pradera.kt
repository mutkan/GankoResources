package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Pradera(
        var _id: String? = null,
        var _sequence: Long? = null,
        var type: String? = null,
        var identificador: Int? = null,
        var idFinca:String? = null,
        var emptyMeadow: Boolean? = null,
        var usedMeadow: Boolean? = null,
        var tamano: Float? = null,
        var tamanoEnHectareas: Boolean? = null,
        var tipoGraminea: String? = null,
        var fechaOcupacion: Date? = null,
        var fechaSalida: Date? = null,
        var mantenimiento: MutableList<Mantenimiento>? = mutableListOf(),
        var aforo: MutableList<Aforo>? = mutableListOf(),
        var group: String? = null,
        var available: Boolean? = null,
        var bovinos:List<String>? =  listOf(),
        var orderValue:Int? = null
) : Parcelable {
    init {
        type = javaClass.simpleName
    }
}

@Parcelize
class Mantenimiento(
        var fechaMantenimiento: Date? = null,
        var producto: String? = null,
        var cantidad: Float? = null,
        var valor: Float? = null,
        var total: Float? = null
) : Parcelable

@Parcelize
class Aforo(
        var valores: MutableList<Float>? = mutableListOf(),
        var promedio: Float? = null,
        var fechaAforo:Date? = null,
        var total:Float? = null
) : Parcelable