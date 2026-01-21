package com.gesnnova.demoges_parking.model

data class CierreRegistroResponse(
    val success: Boolean,
    val message: String,
    val cierre: CierreData? // puede venir null si falla
)

data class CierreData(
    val turno:String,
    val numeroturno:Int,
    val empleado:String,
    val fechaingreso:String,
    val fechasalida:String,
    val totalvehiculos:Int,
    val efectivo:Int,
    val tarjeta:Int,
    val transferencia:Int,
    val otrosingresos:Int,
    val totalabonos:Int,
    val efectivoliquido:Int,
    val totalrecaudado:Int,
    val observaciones:String
)
