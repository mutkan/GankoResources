package com.ceotic.ganko.data.models

import java.util.Date

const val TYPE_ALARM = "Alarm"

class Alarm(
        val _id: String? = null,
        val _sequence: Long? = null,
        val titulo: String? = null,
        val descripcion: String? = null,
        val idAplicacionUno:String? = null,
        val type: String? = null,
        val numeroAplicaciones: Int? = null,
        val aplicacion: Int? = null,
        var grupo: Grupo? = null,
        var bovinos: List<String> = listOf(),
        var noBovinos: List<String> = listOf(),
        var fechaProxima: Date? = null,
        var bovino:AlarmBovine? = null
)

class AlarmBovine(
        val id:String,
        val nombre:String,
        val codigo:String
)