package com.example.demoges_parking.model

data class SalidaRegistroResponse(
    val success: Boolean,
    val message: String,
    val data: SalidaResponseDTO? // puede ser null si falla
)

data class SalidaResponseDTO(
    val numfactura:Int,
    val placa:String,
    val fechaentrada:String,
    val fechasalida:String,
    val tipovehiculo:String,
    val tiposervicio:String,
    val dias:Int,
    val horas:Int,
    val minutos:Int,
    val valor:Int,
    val numerorecibo:String,
    val descuento:Int,
    val total:Int,
    val efectivo:Int,
    val tarjeta:Int,
    val transferencia:Int
)
