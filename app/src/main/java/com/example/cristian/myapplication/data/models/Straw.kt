package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Straw(
        var _id: String? = null,
        var _sequence: Long? = null,
        var type:String ? = null,
        var idFarm: String? = null,
        var idStraw: String? =null,
        var typeStraw: String? = null,
        var layette: String? = null,
        var breed: String? = null,
        var purpose: String? = null,
        var bull: String? = null,
        var origin: String? = null,
        var value: String? = null,
        var state: String? = null

):Parcelable{

    init{
        type = javaClass.simpleName
    }

    override fun toString(): String {
        return this.bull!!
    }

}

