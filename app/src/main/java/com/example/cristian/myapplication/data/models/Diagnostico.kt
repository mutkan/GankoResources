package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Diagnostico(var fecha: Date? = null,
                       var novedad: String? = null) : Parcelable
