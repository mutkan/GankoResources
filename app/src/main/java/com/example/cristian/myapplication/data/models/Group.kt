package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Group(
        val _id: String? = null,
        var _sequence: Long? = null,
        var type: String? = null,
        val finca: String,
        var nombre: String,
        var color: Int,
        var bovines: List<String> = emptyList()
) : Parcelable {

    init {
        type = javaClass.simpleName
    }

}