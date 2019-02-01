package com.ceotic.ganko.data.models

import java.util.Date

const val TYPE_ALARM = "Alarm"
const val ALARM_18_MONTHS = 0
const val ALARM_2_MONTHS = 1
const val ALARM_3_MONTHS = 2
const val ALARM_4_MONTHS = 3
const val ALARM_12_MONTHS = 4

const val ALARM_HEALTH = 5
const val ALARM_VACCINE = 6
const val ALARM_MANAGE = 7

const val ALARM_SECADO = 8
const val ALARM_PREPARACION = 9
const val ALARM_NACIMIENTO = 10

const val ALARM_EMPTY_DAYS_45 = 11
const val ALARM_EMPTY_DAYS_60 = 12
const val ALARM_EMPTY_DAYS_90 = 13
const val ALARM_EMPTY_DAYS_120 = 14

const val ALARM_ZEAL_21 = 15
const val ALARM_ZEAL_42 = 16
const val ALARM_ZEAL_64 = 17
const val ALARM_ZEAL_84 = 18
const val ALARM_DIAGNOSIS = 19


class Alarm(
        val _id: String? = null,
        val _sequence: Long? = null,
        val reference: String?= null,
        val alarma:Int? = null,
        val titulo: String? = null,
        val descripcion: String? = null,
        var idFinca:String? = null,
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