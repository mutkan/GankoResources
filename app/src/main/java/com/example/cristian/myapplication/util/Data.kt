package com.example.cristian.myapplication.util

import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.data.models.Group
import java.util.*

object Data {

    var grupos :MutableList<Group> = mutableListOf(
            Group("Grupo 1", 123,88, listOf("12324","hiuehfe8","wedwd2")),
            Group("Grupo 2", 324,345, listOf("wedwd","hiuehfe8","wedwd2")),
            Group("Grupo 3", 554,544, listOf("csdcsc","hiuehfe8","wedwd2")),
            Group("Grupo 4", 776,754, listOf("dwedw","hiuehfe8","wedwd2")),
            Group("Grupo 5",878, 700, listOf("brtgf","hiuehfe8","wedwd2"))
    )

    var bovines : MutableList<Bovino> = mutableListOf(
            Bovino("w12wedw",232342,"no se","mani manito","Cebu","Manto")
            , Bovino("w12wedw",232342,"no se","mani manito","Cebu","Manto")
            , Bovino("w12wedw",232342,"no se","mani manito","Cebu","Manto")
            , Bovino("w12wedw",232342,"no se","mani manito","Cebu","Manto")


    )
}