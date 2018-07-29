package com.example.cristian.myapplication.data.models

import java.util.*

data class ReporteFuturosPartos(
        val codigo: String,
        val nombre: String?,
        val fechaServicio: Date,
        val fechaEstimadaDeParto: Date
)

data class ReporteSecado(
        val codigo: String,
        val nombre: String?,
        val fechaServicio: Date,
        val fechaSecado: Date
)

data class ReportePreparacion(
        val codigo: String,
        val nombre: String?,
        val fechaEstimadaDeParto: Date,
        val fechaPreparacion: Date
)

data class ReporteDiasVacios(
        val codigo: String,
        val nombre: String?,
        val fechaUltimoParto: Date,
        val fechaUltimoServicio: Date,
        val diasVacios: Long,
        val enServicio:Boolean
)

data class ReportePartosAtendidos(
        val codigo: String,
        val nombre: String?,
        val fechaParto: Date,
        val sexoCria: String,
        val estadoCria: String
)

data class ReporteAbortos(
        val codigo: String,
        val nombre: String?,
        val fechaServicio: Date,
        val fechaConfirmacionAborto: Date
)

data class ReporteTresServicios(
        val codigo: String,
        val nombre: String?,
        val fechaUltimoServicio: Date,
        val diasVacios: Long
)

data class ReporteCelos(
        val codigo: String,
        val nombre: String?,
        val fechaCelo: Date
)

data class ReporteConsolidadoLeche(
        val codigo: String,
        val totalProduccion: Int,
        val valor: Int
)

data class ReporteLeche(
        val codigo: String,
        val nombre: String?,
        val fechaUltimoParto: Date,
        val diasEnProduccion: Int,
        val promedioProduccion: Float,
        val totalProduccion: Int
)

data class  ReporteDestetos(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val fechaDestete: Date,
        val codigoMadre: String,
        val nombreMadre: String
)

data class ReporteGananciaPeso(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val GDP: Float,
        val proposito: String
)

data class ReporteVacunas(
        val codigo: String,
        val nombre: String?,
        val fechaVacunacion: Date,
        val vacunaAplicada: String
)

data class ReporteSanidad(
        val codigo: String,
        val nombre: String?,
        val fechaEvento: Date,
        val evento: String,
        val diagnostico: String,
        val producto: String
)

data class ReporteManejo(
        val codigo: String,
        val nombre: String?,
        val fechaEvento: Date,
        val evento: String,
        val tratamiento: String,
        val producto: String
)

data class ReportePraderas(
        val numero: Int,
        val tipoGraminea: String,
        val fechaFertilizacion: Date,
        val producto: String,
        val cantidad: Float
)

data class ReporteOcupacionPraderas(
        val numero: Int,
        val fechaOcupacion: Date,
        val fechaDescanso: Date,
        val fechaFertilizacion: Date
)

data class ReporteAlimentacion(
        val codigo: String,
        val nombre: String?,
        val racion: Int,
        val producto: String,
        val valorTotal: Int
)

data class ReporteSuplementosUsados(
        val nombre: String?,
        val totalUsado: String,
        val valorTotal:Int
)

data class ReporteAnimalesEnPradera(
        val lote: Int,
        val codigo: String,
        val nombre: String?,
        val fechaIngreso: Date,
        val fechaSalida: Date
)

data class ReportePajillas(
        val codigo: String,
        val canastilla: Int,
        val raza: String,
        val proposito: String,
        val toro: String,
        val procedencia: String
)

data class ReporteInventario(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val proposito: String,
        val color: String,
        val raza: String,
        val numeroPartos: Int,
        val codigoMadre: String,
        val codigoPadre: String
)

data class ReporteTernerasEstaca(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val proposito: String,
        val raza: String,
        val codigoMadre: String,
        val codigoPadre: String
)

data class ReporteTernerasDestetas(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val proposito: String,
        val raza: String,
        val codigoMadre: String,
        val codigoPadre: String
)

data class ReporteNovillasLevante(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val proposito: String,
        val raza: String
)

data class ReporteNovillasVientre(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val proposito: String,
        val raza: String
)


data class ReporteHorras(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val proposito: String,
        val raza: String
)


data class ReporteVacas(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val proposito: String,
        val raza: String
)

data class ReporteSalidas(
        val codigo: String,
        val nombre: String?,
        val fechaSalida: Date,
        val tipoSalida: String
)

fun getHeader(tipoReporte:String):List<String> =

    when(tipoReporte){
         "Partos"-> listOf("")
         "Secado"-> listOf("Codigo","Nombre","Fecha servicio","Fecha secado")
         "Preparación"-> listOf("Codigo","Nombre","Fecha de parto","Fecha de preparación")
         "Días abiertos"-> listOf("Codigo","Nombre","Ultimo parto","Ultimo servicio","Dias vacios","En servicio")
         "Partos atendidos"-> listOf("Codigo","Nombre","Fecha parto","Sexo cria", "Estado cria")
         "Abortos"-> listOf("Codigo","Nombre","Fecha servicio","Fecha confirmacion aborto")
         "Tres servicios"-> listOf("Codigo","Nombre","Ultimo servicio","Dias vacios")
         "Celos"-> listOf("Codigo","Nombre","Fecha celo")
         "Resumen reproductivo"-> listOf()
         //LECHE
         "Consolidado de leche"-> listOf("Codigo","Total produccion","Valor")
         "Reporte de leche"-> listOf("Codigo","Nombre","Ultimo parto","En produccion","Promedio produccion","Total produccion")
         //CEBA
         "Destetos"-> listOf("Codigo","Nombre","Fecha nacimiento","Fecha destete","Codigo madre","Nombre madre")
         "Ganancia diaria de peso"-> listOf("Codigo","Nombre","Fecha nacimiento","GDP","Proposito")
         //PRADERAS
         "Praderas"-> listOf("Numero","Tipo gaminea","Fecha fertilizacion","Producto","Cantidad")
         "Ocupación de praderas"-> listOf("Numero","Fecha ocupacion","Fecha descanso","Fecha fertilización")
         //ALIMENTACION
         "Alimentacion"-> listOf("Codigo","Nombre","Racion","Producto","Valor total")
         "Suplementos usados"-> listOf("Nombre","Total usado","Valor total")
         //MOVIMIENTOS
         "Animales en pradera"-> listOf("Lote","Codigo","Nombre","Fecha ingreso","Fecha salida")
         //ENTRADAS
         "Inventario"-> listOf("Codigo","Nombre","Nacimiento","Propósito","Color","Raza","Partos","C.Madre","C.Padre")
         "Terneras en estaca"-> listOf("Codigo","Nombre","Nacimiento","Proposito","Raza","C.Madre", "C.Padre")
         "Terneras destetas"-> listOf("Codigo","Nombre","Nacimiento","Proposito","Raza","C.Madre","C.Padre")
         "Novillas de levante"-> listOf("Codigo","Nombre","Nacimiento","Proposito","Raza")
         "Novillas vientre"-> listOf("Codigo","Nombre","Nacimiento","Proposito","Raza")
         "Horras"-> listOf("Codigo","Nombre","Nacimiento","Proposito","Raza")
         "Vacas"-> listOf("Codigo","Nombre","Nacimiento","Proposito","Raza")
         //SALIDAS
         "Salida"-> listOf("Codigo","Nombre","Fecha salida","Tipo salida")
         //VACUNAS
         "Vacunas"-> listOf("Codigo","Nombre","Fecha vacunacion","Vacuna aplicada")
         //SANIDAD
         "Sanidad"-> listOf("Codigo","Nombre","Fecha evento","Evento","Diagnostico","Producto")
         //MANEJO
         "Manejo"-> listOf("Codigo","Nombre","Fecha evento","Evento","Tratamiento","Producto")
         //PAJILLAS
         "Pajillas"-> listOf("Codigo","Canastilla","Proposito","Toro","Procedencia")
         else -> emptyList()


        }

