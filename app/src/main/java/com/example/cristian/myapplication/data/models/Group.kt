package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
 data class  Group(
        var nombre:String?=null,
        var numero:Int?=null,
        var color:Int?=null,
        var BovinesId : List<String>? = null,
        var type: String? = null
): Parcelable {
     init {
         type = javaClass.simpleName
     }
 }