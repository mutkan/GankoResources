package com.example.cristian.myapplication.data.models

import java.util.*

class SalidaLeche(
        var idFarm: String? = null,
        var tipo: String? = null,
        var fecha: Date? = null,
        var operacion: String? = null,
        var valorLitro: Int? = null,
        var numeroLitros: Int? = null,
        var totalLitros: Int? = null
){}