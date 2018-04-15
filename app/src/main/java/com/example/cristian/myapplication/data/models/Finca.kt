package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Finca(
        var usuarioId: String,
        var nombre: String,
        var ubicacion: String,
        var hectareas: Int) : CouchEntity(), Parcelable