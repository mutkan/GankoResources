package com.ceotic.ganko.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
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
        val nombre: String,
        val litros: Int,
        val fecha_produccion: Date
)

data class  ReporteDestetos(
        val codigo: String,
        val nombre: String?,
        val fechaNacimiento: Date,
        val fechaDestete: Date,
        val codigoMadre: String,
        val codigoPadre: String
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
@Parcelize
data class Promedio(
        val tipo:String,
        val total:Number,
        val bovino:String? = null,
        val individual:Number? = null,
        val desde:Date? = null,
        val hasta:Date? = null,
        val mes:Int? = null,
        val anio:Int? = null
) : Parcelable

//region heeader pdf and excel file
fun getHeader(tipoReporte:String):List<String> =

    when(tipoReporte){
         "Partos futuros"-> listOf("Codigo","Nombre","F. servicio","F. estimada de parto")
         "Secado"-> listOf("Codigo","Nombre","Fecha servicio","Fecha secado")
         "Preparación"-> listOf("Codigo","Nombre","Fecha de parto","Fecha de monta","Fecha de preparación")
         "Días abiertos"-> listOf("Codigo","Nombre","Ultimo parto","Ultimo servicio","Dias vacios","En servicio")
         "Partos atendidos"-> listOf("Codigo","Nombre","Fecha parto","Sexo cria", "Estado cria")
         "Abortos"-> listOf("Codigo","Nombre","Fecha servicio","Fecha confirmacion aborto")
         "Tres servicios"-> listOf("Codigo","Nombre","Ultimo servicio","Dias vacios")
         "Celos"-> listOf("Codigo","Nombre","Fecha celo")
         "Resumen reproductivo"-> listOf()
         //LECHE
         "Consolidado de leche"-> listOf("Tipo","Fecha","Operacion","Numero Litros","Valor litros","Total Litros")
         "Reporte de leche"-> listOf("Codigo del bovino","Numero de litros","Fecha de producción","Jornada")
         //CEBA
         "Destetos"-> listOf("Codigo","Nombre","Fecha nacimiento","Fecha destete","Codigo madre","Nombre madre")
         "Ganancia diaria de peso"-> listOf("Codigo","Nombre","Fecha nacimiento","GDP","Proposito")
         //PRADERAS
         "Praderas"-> listOf("Numero","Tipo gaminea","Fecha fertilizacion","Producto","Cantidad")
         "Ocupación de praderas"-> listOf("Numero","Graminea","Fecha de manetenimiento","Fecha ocupacion","Fecha descanso","Fecha fertilización")
         //ALIMENTACION
         "Alimentación"-> listOf("Codigos","Tipo de alimento","Ración","Precio total")
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
         "Pajillas"-> listOf("Codigo","Canastilla","Raza","Proposito","Toro","Procedencia")
         else -> emptyList()
        }



//endregion

