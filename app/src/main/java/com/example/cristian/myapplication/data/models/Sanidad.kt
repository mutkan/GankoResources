package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Sanidad() : Parcelable {

    var fecha: Date? = null
    var bovino: String? = null
    var evento: String? = null
    var nombre: String? = null
    var otra: String? = null
    var diagnostico: String? = null
    var tratamiento: String? = null
    var producto: String? = null
    var dosis: String? = null
    var via: String? = null
    var numeroAplicaciones: Int? = null
    var frecuencia: String? = null
    var observaciones: String? = null
    var aplicaciones: List<Aplicacion>? = null

    constructor(fecha:Date, bovino: String,  evento:String, nombre:String, otra: String, diagnostico: String, tratamiento: String,
                producto: String, dosis: String, via: String, numeroAplicaciones: Int, frecuencia: String,
                observaciones: String, aplicacion: List<Aplicacion>):this(){
        this.fecha = fecha
        this.bovino = bovino
        this.evento = evento
        this.nombre = nombre
        this.otra = otra
        this.diagnostico = diagnostico
        this.tratamiento = tratamiento
        this.producto = producto
        this.dosis = dosis
        this.via = via
        this.numeroAplicaciones = numeroAplicaciones
        this.frecuencia = frecuencia
        this.observaciones = observaciones
        this.aplicaciones = aplicaciones
    }
}