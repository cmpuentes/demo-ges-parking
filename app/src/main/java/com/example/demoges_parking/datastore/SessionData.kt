package com.example.demoges_parking.datastore

data class SessionData(
    val token: String = "",
    val nombreCompleto: String = "",
    val fechaInicio: String = "",
    val turno: String = "",
    val numeroTurno: Int = 0,
    val turnoFinalizado: Boolean = false
)
