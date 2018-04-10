package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Produccion(
        var bovino: String,
        var jornada: String,
        var litros: String,
        var fecha: Date
):CouchEntity(),Parcelable {}