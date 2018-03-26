package com.example.cristian.myapplication.data.models

import java.util.Date

data class Feed(var localId: Int?,
                var id: Int?,
                var idBovino: Int,
                var tipo: String,
                var bovino: String,
                var fecha: Date,
                var racion: Int,
                var valorKilo: Int,
                var otro: String
              ){}