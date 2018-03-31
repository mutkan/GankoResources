package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Ceba (var localId:Int?,
            var tipo:String,
            var codigoBovino:String,
            var fecha:Date,
            var peso:Float,
            var gananciaPeso:Float): Parcelable {}