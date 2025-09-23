package com.example.demoges_parking.model

data class LoginRequest(
    val login: String ="",
    val password: String ="",
    val turno: String ="",
    val fecha_inicio: String =""
)
