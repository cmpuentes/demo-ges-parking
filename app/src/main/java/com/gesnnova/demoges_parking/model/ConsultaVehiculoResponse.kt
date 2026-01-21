package com.gesnnova.demoges_parking.model

data class ConsultaVehiculoResponse(
    val placa:String ="",
    val fechaIngreso:String ="",
    val cliente:String ="",
    val tipoVehiculo:String ="",
    val tipoServicio:String ="",
    val numeroTurno:Int =0,
    val empleado:String ="",
    val dias:Int =0,
    val horas:Int =0,
    val minutos:Int =0,
    val valor:Int =0
)

data class ConsultaVehiculoResult(
    val success: Boolean,
    val message: String,
    val data: ConsultaVehiculoResponse?,  // o directamente ConsultaVehiculoData si la tuya se llama así
)
