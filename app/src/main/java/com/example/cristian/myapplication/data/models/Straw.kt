package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Straw(
        var idFarm: String? = null,
        var idStraw: String? =null,
        var typeStraw: String? = null,
        var layette: String? = null,
        var race: String? = null,
        var purpose: String? = null,
        var bull: String? = null,
        var origin: String? = null,
        var value: String? = null,
        var state: String? = null,
        var type:String ? = null
):Parcelable{

    init{
        type = javaClass.simpleName
    }

}

