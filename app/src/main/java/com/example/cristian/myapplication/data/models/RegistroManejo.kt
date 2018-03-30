package com.example.cristian.myapplication.data.models

import java.util.*

class RegistroManejo(
        var tipo: String,
        var fecha: Date,
        var manejo: Manage,
        var bovinos: Array<String>
){}