package com.gesnnova.demoges_parking.model

data class SalidaReq(
    val idingreso: Int,
    val placa: String,
    val tipovehiculo: String,
    val tiposervicio: String,
    val cliente: String,
    val fechaingreso: String,
    val fechasalida: String,
    val zona: String,
    val dias: Int,
    val horas: Int,
    val minutos: Int,
    val costototal: Int,
    val numerorecibo: Int,
    val descuento: Int,
    val subtotal: Int,
    val efectivo: Int,
    val tarjeta: Int,
    val transferencia: Int,
    val total: Int,
    val turno: String,
    val turnoentrada: Int,
    val empleadoentrada: String,
    val turnosalida: Int,
    val empleadosalida: String
)
