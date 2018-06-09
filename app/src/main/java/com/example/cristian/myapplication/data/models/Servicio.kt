package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Servicio(
        var fecha: Date? = null,
        var fechaUltimoCelo: Date? = null,
        var condicionCorporal: Int? = null,
        var empadre: String? = null,
        var codigoToro: String? = null,
        var pajilla: String? = null,
        var diagnosticos: List<Diagnostico>? = listOf(),
        var parto: List<Parto>? = listOf(),
        var confirmacion: Boolean? = null,
        var novedad:String? = null,
        var posFechaParto:Date? = null
):Parcelable