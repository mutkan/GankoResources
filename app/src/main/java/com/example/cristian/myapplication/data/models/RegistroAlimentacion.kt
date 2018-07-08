package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
@Parcelize
class  RegistroAlimentacion(var _id: String? = null,
                            var sequence: Long? = null,
                            var type: String? = null,
                            var grupo: Group? = null,
                            var fecha:Date? =null,
                            var idFinca: String? = null,
                            var tipoAlimento: String? = null,
                            var peso:Int? =null,
                            var valorkg:Int? =null,
                            var valorTotal:Int? =null,
                            var bovinos: List<String>? = listOf()) : Parcelable {
    init {
        type = javaClass.simpleName
    }
}