package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Novedad(val fecha: Date,
                   val novedad: String) : Parcelable
