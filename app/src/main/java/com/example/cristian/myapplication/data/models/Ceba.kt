package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Ceba() : CouchEntity(), Parcelable {
    var tipo: String? = null
    var bovino: String? = null
    var fecha: Date? = null
    var peso: Float? = null
    var gananciaPeso: Float? = null

    constructor(tipo: String, bovino: String, fecha: Date, peso: Float, gananciaPeso: Float) : this() {
        this.tipo = tipo
        this.bovino = bovino
        this.fecha = fecha
        this.peso = peso
        this.gananciaPeso = gananciaPeso
    }
}