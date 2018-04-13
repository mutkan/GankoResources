package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Manage() :Parcelable{
        var fecha: Date? = null
        var bovino: String? = null
        var numeroAplicaciones: Int? = null
        var frecuencia: Int? = null
        var tipo: String? = null
        var otro: String? = null
        var tratamiento: String? = null
        var producto: String? = null
        var observaciones: String? = null
        var aplicaciones: List<Aplicacion>? = null

    constructor(fecha: Date, bovino: String, numeroAplicaciones: Int, frecuencia: Int, tipo: String, otro: String,
                tratamiento: String, producto: String, observaciones: String, aplicaciones: List<Aplicacion>): this(){

        this.fecha = fecha
        this.bovino = bovino
        this.numeroAplicaciones = numeroAplicaciones
        this.frecuencia = frecuencia
        this.tipo = tipo
        this.otro = otro
        this.tratamiento = tratamiento
        this.producto = producto
        this.observaciones = observaciones
        this.aplicaciones = aplicaciones
    }
}