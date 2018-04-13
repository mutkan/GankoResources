package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Diagnostico() : CouchEntity(), Parcelable{
        var fecha: Date? = null
        var novedad: String? = null

    constructor(fecha: Date, novedad: String): this(){
        this.fecha = fecha
        this.novedad = novedad
    }
}