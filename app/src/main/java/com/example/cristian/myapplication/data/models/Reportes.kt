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
        val diasVacios: Long
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