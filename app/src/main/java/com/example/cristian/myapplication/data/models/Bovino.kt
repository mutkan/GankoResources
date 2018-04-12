package com.example.cristian.myapplication.data.models

import com.example.cristian.myapplication.data.db.CouchEntity
import java.util.*

class Bovino(
        var idBovino: String,
        var tipo: String,
        var codigo: String,
        var imagenRemota: String,
        var imagenLocal: String,
        var nombre: String,
        var fechaNacimiento: Date?,
        var fechaIngreso: Date,
        var genero: String,
        var proposito: String,
        var peso: Int,
        var color: String,
        var raza: String,
        var codigoMadre: String?,
        var codigoPadre: String?,
        var lote: Int,
        var partos: Int?,
        var precioCompra: Int?,

        var retirado: Boolean,
        var fechaSalida: Date?,
        var motivoSalida: String?,
        var precioVenta: Int?,

        var finca: String,

        var destete: Boolean,
        var fechaDestete: Date?,
        var celos: Array<Date>,
        var servicios: Array<Servicio>,
        var vacunas: Array<Vacuna>,
        var sanidad: Array<Sanidad>,
        var manejo: Array<Manage>
):CouchEntity(){}