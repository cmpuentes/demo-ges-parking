package com.example.demoges_parking.model

data class HistorialTurnoResult(
    val success:Boolean,
    val message:String,
    val data:HistorialTurnoResponse
)

data class HistorialTurnoResponse(
    val totalIngresos:Int,
    val totalSalidas:Int,
    val ingresos:List<IngresoDTO>,
    val salidas:List<SalidaDTO>
)

data class IngresoDTO(
    val placa:String,
    val tipoVehiculo:String,
    val tipoServicio:String,
    val zona:String,
    val fechaIngreso:String
)

data class SalidaDTO(
    val idsalida:Int,
    val placa:String,
    val tipoVehiculo:String,
    val tipoServicio:String,
    val fechaSalida:String,
    val total:Int
)
