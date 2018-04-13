package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Produccion():CouchEntity(),Parcelable {

        var bovino: String? = null
        lateinit var jornada: String
        lateinit var litros: String
        lateinit var fecha: Date

        constructor(bovino:String, jornada:String, litros:String, fecha:Date):this(){
            this.bovino = bovino
            this.jornada = jornada
            this.litros = litros
            this.fecha = fecha
        }
}