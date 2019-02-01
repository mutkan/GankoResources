package com.ceotic.ganko.data.models

import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

@Parcelize
class Bovino(
        var _id: String? = null,
        var _sequence: Long? = null,
        var type: String? = null,
        var tipo: String? = null,
        var codigo: String? = null,
        var imagen: String? = null,
        var nombre: String? = null,
        var fechaNacimiento: Date? = null,
        var fechaIngreso: Date? = null,
        var genero: String? = null,
        var proposito: String? = null,
        var peso: Int? = null,
        var color: String? = null,
        var raza: String? = null,
        var codigoMadre: String? = null,
        var codigoPadre: String? = null,
        var lote: Int? = null,
        var partos: Int? = null,
        var precioCompra: Int? = null,
        var procedencia: String? = null,

        var retirado: Boolean? = null,
        var fechaSalida: Date? = null,
        var motivoSalida: String? = null,
        var precioVenta: Int? = null,

        var finca: String? = null,

        var destete: Boolean? = null,
        var fechaDestete: Date? = null,
        var celos: List<Date>? = null,

        var fechaProximoCelo: Date? = null,

        var seleccionado: Boolean? = false,
        var serviciosFallidos: Int? = null,

        var servicios: List<Servicio>? = listOf(),
        var vacunas: List<Vacuna>? = listOf(),
        var sanidad: List<Sanidad>? = listOf(),
        var manejo: List<Manage>? = listOf()

) : Parcelable {
    init {
        type = javaClass.simpleName
    }

    override fun toString(): String {
        return this.codigo!!
    }

    companion object {
        const val ALERT_DIAGNOSIS = "diagnostico"
        const val ALERT_21_DAYS_AFTER_SERVICE = "21DiasDesdeServicio"
        const val ALERT_3_FAILED_SERVICES= "3ServiciosFallidos"

        const val ALERT_EMPTY_DAYS = "diasVacios"
        const val ALERT_45_EMPTY_DAYS = "45DiasVacios"
        const val ALERT_60_EMPTY_DAYS = "60DiasVacios"
        const val ALERT_90_EMPTY_DAYS = "90DiasVacios"
        const val ALERT_120_EMPTY_DAYS = "120DiasVacios"

        const val ALERT_PREPARATION = "preparacion"
        const val ALERT_BIRTH = "parto"
        const val ALERT_DRYING = "secado"

        const val ALERT_ZEAL = "celo"


    }
}