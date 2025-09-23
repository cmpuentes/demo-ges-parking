package com.example.demoges_parking.model

data class ResumenTurnoResponse(
    val vehiculosSalida: Int,
    val efectivo: Int,
    val tarjeta: Int,
    val transferencia: Int,
    val totalRecaudado: Int
)
