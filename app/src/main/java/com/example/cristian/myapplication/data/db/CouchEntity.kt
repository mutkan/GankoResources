package com.example.cristian.myapplication.data.db

import com.fasterxml.jackson.annotation.JsonIgnore

open class CouchEntity{

    var _id:String? = null
    var _sequence:Long? = null
    var type:String = javaClass.simpleName


}