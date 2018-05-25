package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data  class Parto(
        var fecha: Date,
        var intervalo: Int,
        var diasVacios: Int,
        var sexoCria: String,
        var estadoCria: String
):Parcelable