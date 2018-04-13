package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
class Feed() : CouchEntity(), Parcelable {

    var tipo: String? = null
    var bovino: String? = null
    var fecha: Date? = null
    var racion: Int? = null
    var valorKilo: Int? = null
    var otro: String? = null

    constructor(tipo: String, bovino: String, fecha: Date, racion : Int, valorKilo: Int
                , otro: String):this(){

        this.tipo = tipo
        this.bovino = bovino
        this.fecha = fecha
        this.valorKilo = valorKilo
        this.otro = otro
    }
}