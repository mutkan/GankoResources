package com.ceotic.ganko.data.models

import java.util.*

class RegistroSanidad(
        var tipo: String,
        var fecha: Date,
        var evento: Sanidad,
        var bovinos: Array<String>
){}