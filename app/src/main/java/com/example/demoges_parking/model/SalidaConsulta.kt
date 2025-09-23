package com.example.demoges_parking.model

data class SalidaConsulta(
    val idingreso:Int,
    val fechaingreso:String,
    val cliente:String,
    val zona:String,
    val tipovehiculo:String,
    val tiposervicio:String,
    val numeroturno:Int,
    val turno:String,
    val empleado:String
)
