package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Group(
    val _id:String? = null,
    val idFinca:String,
    var nombre:String,
    var numero:Int,
    var color:Int,
    var bovines:List<String> = emptyList(),
    var type:String? = null
) : Parcelable{

    init {
        type = javaClass.simpleName
    }

}