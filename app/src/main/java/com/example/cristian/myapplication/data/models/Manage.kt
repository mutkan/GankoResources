package com.example.cristian.myapplication.data.models

import java.util.*

class Manage(
        var fecha: Date,
        var numeroAplicaciones: Int,
        var frecuencia: Int,
        var tipo: String,
        var otro: String?,
        var tratamiento: String,
        var producto: String,
        var observaciones: String,
        var aplicaciones: Array<Aplicacion>
){}