package com.example.cristian.myapplication.data.models

import java.util.Date

class Alarm(
        val _id: String,
        val _sequence:Long,
        val title: String,
        val description: String,
        val type: String,
        val numeroAplicaciones: Int? = null,
        val aplicacion: Int? = null,
        var grupo: Grupo? = null,
        var bovinos: List<String> = listOf(),
        var noBovinos: List<String> = listOf(),
        var fechaProxima: Date? = null
)