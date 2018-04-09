package com.example.cristian.myapplication.data.db

import com.fasterxml.jackson.annotation.JsonIgnore

open class CouchEntity{

    @JsonIgnore
    var _id:String? = null

    var sequence:Long? = null
    var type:String = javaClass.simpleName


}