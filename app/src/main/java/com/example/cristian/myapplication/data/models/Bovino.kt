package com.example.cristian.myapplication.data.models

import android.os.Parcelable
import com.example.cristian.myapplication.data.db.CouchEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Bovino(): CouchEntity(), Parcelable{

    var tipo: String? = null
    var codigo: String? = null
    var imagenRemota: String? = null
    var imagenLocal: String? = null
    var nombre: String? = null
    var fechaNacimiento: Date? = null
    var fechaIngreso: Date? = null
    var genero: String? = null
    var proposito: String? = null
    var peso: Int? = null
    var color: String? = null
    var raza: String? = null
    var codigoMadre: String? = null
    var codigoPadre: String? = null
    var lote: Int? = null
    var partos: Int? = null
    var precioCompra: Int? = null

    var retirado: Boolean? = null
    var fechaSalida: Date? = null
    var motivoSalida: String? = null
    var precioVenta: Int? = null

    var finca: String? = null

    var destete: Boolean? = null
    var fechaDestete: Date? = null
    var celos: List<Date>? = null
    var servicios: List<Servicio>? = null
    var vacunas: List<Vacuna>? = null
    var sanidad: List<Sanidad>? = null
    var manejo: List<Manage>? = null

    constructor( tipo: String, codigo: String, imagenRemota: String, imagenLocal: String,
                 nombre: String, fechaNacimiento: Date, fechaIngreso: Date, genero: String,
                 proposito: String, peso: Int, color: String, raza: String, codigoMadre: String,
                 codigoPadre: String, lote: Int, partos: Int, precioCompra: Int, retirado: Boolean,
                 fechaSalida: Date, motivoSalida: String, precioVenta: Int, finca: String,
                 destete: Boolean, fechaDestete: Date, celos: List<Date>, servicios: List<Servicio>,
                 vacunas: List<Vacuna>, sanidad: List<Sanidad>, manejo: List<Manage>): this(){

        this.tipo = tipo
                this.codigo = codigo
                this.imagenRemota = imagenRemota
                this.imagenLocal = imagenLocal
                this.nombre = nombre
                this.fechaNacimiento = fechaNacimiento
                this.fechaIngreso = fechaIngreso
                this.genero = genero
                this.proposito = proposito
                this.peso = peso
                this.color = color
                this.raza = raza
                this.codigoMadre = codigoMadre
                this.codigoPadre = codigoPadre
                this.lote = lote
                this.partos = partos
                this.precioCompra = precioCompra

                this.retirado = retirado
                this.fechaSalida = fechaSalida
                this.motivoSalida = motivoSalida
                this.precioVenta = precioVenta

                this.finca = finca

                this.destete = destete
                this.fechaDestete = fechaDestete
                this.celos = celos
                this.servicios = servicios
                this.vacunas = vacunas
                this.sanidad = sanidad
                this.manejo = manejo

    }

}