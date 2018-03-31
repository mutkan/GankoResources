package com.example.cristian.myapplication.data.models

import java.util.*

class Sanidad(
        var fecha: Date,
        var evento: String,
        var nombre: String,
        var otra: String?,
        var diagnostico: String,
        var tratamiento: String,
        var producto: String,
        var dosis: String,
        var via: String,
        var numeroAplicaciones: Int,
        var frecuencia: String,
        var observaciones: String,
        var aplicaciones: Array<Aplicacion>
){}