package com.example.demoges_parking.model

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val nombreCompleto: String?,
    val fecha_inicio: String?,
    val turno: String?,
    val numero_turno: Int?
)
