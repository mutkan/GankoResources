package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Servicio(
        var fecha: Date? = null,
        var fechaUltimoCelo: Date? = null,
        var condicionCorporal: Double? = null,
        var empadre: String? = null,
        var codigoToro: String? = null,
        var pajilla: String? = null,
        var diagnostico: Diagnostico? = null,
        var parto: Parto? = null,
        var novedad: Novedad? = null,
        var posFechaParto:Date? = null,
        var finalizado: Boolean? = null
):Parcelable