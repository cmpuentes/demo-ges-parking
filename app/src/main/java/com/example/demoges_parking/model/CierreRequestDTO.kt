package com.example.demoges_parking.model

data class CierreRegistroRequest(
    val turno: String,
    val numeroturno: Int,
    val empleado: String,
    val fechaingreso: String,
    val fechasalida: String,
    val recibidos: Int,
    val totalvehiculos: Int,
    val base: Int,
    val efectivo: Int,
    val tarjeta: Int,
    val transferencia: Int,
    val otrosingresos: Int,
    val efectivoliquido: Int,
    val totalrecaudado: Int,
    val observaciones: String,
    val totalabonos: Int

)
