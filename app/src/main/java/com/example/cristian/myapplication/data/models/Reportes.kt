package com.example.cristian.myapplication.data.models

import java.util.*

data class ReportePartos(
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