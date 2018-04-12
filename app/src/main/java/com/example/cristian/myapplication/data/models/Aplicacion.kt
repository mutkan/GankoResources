package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Aplicacion() : Parcelable{
        var fecha: Date? = null
        var aplicado: Boolean? = null
        var valorProducto: Int? = null
        var valorAsistencia: Int? = null

    constructor(fecha: Date, aplicado: Boolean, valorProducto: Int, valorAsistencia: Int): this(){
        this.fecha = fecha
        this.aplicado = aplicado
        this.valorProducto = valorProducto
        this.valorAsistencia = valorAsistencia
    }
}