package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass

@Parcelize
data class RegistroVacuna(var _id:String? =  null,
                          var _sequence:Long? =  null,
                          var type:String? =  null,
                          var idDosisUno:String? =  null,
                          var fecha:Date? =  null,
                          var fechaProx:Date? =  null,
                          var nombre:String? =  null,
                          var dosis:Int? =  null,
                          var aplicacion:Int? =  null,
                          var numeroAplicaciones:Int? =  null,
                          var frecuencia: String? =  null,
                          var grupo: Grupo? =  null,
                          var bovinos: List<Bovinos>? =  null
) : Parcelable {
    init {
        type = javaClass.simpleName
    }
}