package com.example.demoges_parking.model

data class VehiculoActivoDTO(
    val placa: String,
    val tipoVehiculo: String,
    val tipoServicio: String,
    val fechaIngreso: String,
    val zona: String
)

data class VehiculosActivosResponse(
    val total: Int,
    val vehiculos: List<VehiculoActivoDTO>
)

data class ApiResponseActivos(
    val success: Boolean,
    val message: String,
    val data: VehiculosActivosResponse?
)
