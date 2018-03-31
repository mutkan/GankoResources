package com.example.cristian.myapplication.data.models

import java.util.*

class Servicio(
        var fecha: Date,
        var fechaUltimoCelo: Date,
        var condicionCorporal: Int,
        var  empadre: String,
        var codigoToro: String?,
        var pajilla: String?,
        var diagnosticos: Array<Diagnostico>,
        var parto: Array<Parto>,
        var confirmacion: Boolean
){}