package com.example.demoges_parking.model

data class IngresoResponse(
    val success: Boolean, // ✅ corregido
    val message: String,
    val data: IngresoData
)

//Datos para impresión
data class IngresoData(
    val placa: String ="",
    val fechaIngreso: String ="",
    val cliente: String ="",
    val zona: String ="",
    val tipoVehiculo: String ="",
    val numeroTurno: Int =0,
    val empleado: String =""
)
