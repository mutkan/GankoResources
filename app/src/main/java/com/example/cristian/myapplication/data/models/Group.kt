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
        var bovines: List<String> = emptyList(),
        var pradera: String? = null
) : Parcelable {

    init {
        type = javaClass.simpleName
    }

    override fun toString(): String {
        return this.nombre
    }

}

fun Group.toGrupo():Grupo = Grupo(this._id,this.nombre, this.color)