package com.example.demoges_parking.model

data class TarifaResponse(
    val success: Boolean,
    val message: String?,
    val data: Tarifa?
)

data class Tarifa(
    val precio12h: Int,
    val descuentorecibo: Int,
    val preciohoras: Int
)
