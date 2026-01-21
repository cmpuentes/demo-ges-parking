package com.gesnnova.demoges_parking.model

data class ResumenTurnoResponse(
    val vehiculosSalida: Int,
    val abonoEfectivo: Int,
    val abonoTarjeta: Int,
    val abonoTransferencia: Int,
    val totalAbonos: Int,
    val efectivo: Int,
    val tarjeta: Int,
    val transferencia: Int,
    val totalRecaudado: Int
)
