package com.example.cristian.myapplication.data.models

import java.util.*


data class RegistroVacuna(var tipo: String,
                          var fecha: Date,
                          var vacuna: Vacuna,
                          var bovinos: List<String>
)  {
}