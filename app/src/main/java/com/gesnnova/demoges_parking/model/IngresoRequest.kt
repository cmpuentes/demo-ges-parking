package com.gesnnova.demoges_parking.model

data class IngresoRequest(
    val turno: String ="",
    val placa: String ="",
    val fechaingreso: String ="",
    val tipovehiculo: String ="",
    val tiposervicio: String ="",
    val cliente: String ="",
    val zona: String ="",
    val observaciones: String ="",
    val numeroturno: Int =0,
    val empleado: String =""
)
