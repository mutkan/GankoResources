package com.ceotic.ganko.data.models

import java.util.Date

const val TYPE_ALARM = "Alarm"
const val ALARM_18_MONTHS = 0
const val ALARM_2_MONTHS = 1
const val ALARM_3_MONTHS = 2
const val ALARM_4_MONTHS = 3
const val ALARM_12_MONTHS = 4

class Alarm(
        val _id: String? = null,
        val _sequence: Long? = null,
        val reference: String?= null,
        val alarma:Int? = null,
        val titulo: String? = null,
        val descripcion: String? = null,
        val idFinca:String? = null,
        val idAplicacionUno:String? = null,
        val type: String? = null,
        val numeroAplicaciones: Int? = null,
        val aplicacion: Int? = null,
        var grupo: Grupo? = null,
        var bovinos: List<String> = listOf(),
        var noBovinos: List<String> = listOf(),
        var fechaProxima: Date? = null,
        var bovino:AlarmBovine? = null,
        var device:List<AlarmDevice> = emptyList(),
        var activa:Boolean = true
)

class AlarmBovine(
        val id:String,
        val nombre:String,
        val codigo:String
)

class AlarmDevice(
        val device:Long,
        val uuid:String
)