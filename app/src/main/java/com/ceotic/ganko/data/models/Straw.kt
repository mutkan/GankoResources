package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Straw(
        var _id: String? = null,
        var _sequence: Long? = null,
        var type:String ? = null,
        var idFarm: String? = null,
        var fecha: Date? = null,
        var idStraw: String? =null,
        var layette: String? = null,
        var breed: String? = null,
        var purpose: String? = null,
        var bull: String? = null,
        var origin: String? = null,
        var value: String? = null,
        var state: Int? = UNUSED_STRAW

):Parcelable{

    init{
        type = javaClass.simpleName
    }

    override fun toString(): String {
        return "Canastilla: ${this.layette}, Id: ${this.idStraw}"
    }

    companion object {
        const val UNUSED_STRAW = 0
        const val USED_STRAW = 1
    }

}

