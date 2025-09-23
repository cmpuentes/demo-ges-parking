package com.example.demoges_parking.model

data class CierreResult(
    val success: Boolean,
    val message: String?,
    val data: CierreResponseDTO?
)

data class CierreResponseDTO(
    val totalVehiculosActivos: Int,
    val totalEfectivo: Int,
    val totalTarjeta: Int,
    val totalTransferencia: Int,
    val totalEfectivoAbono: Int,
    val totalTarjetaAbono: Int,
    val totalTransferenciaAbono: Int
)
