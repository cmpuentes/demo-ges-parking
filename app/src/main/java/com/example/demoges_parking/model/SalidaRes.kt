package com.example.demoges_parking.model

data class SalidaRes(
    val success: Boolean,
    val message: String? = null,
    val data: SalidaData? = null
)
data class SalidaData(
    val idingreso:Int,
    val fechaingreso: String, // "2023-10-01T10:00:00"
    val cliente: String,
    val zona: String,
    val tipovehiculo: String,
    val tiposervicio: String,
    val numeroturno: Int,
    val empleado: String
)
