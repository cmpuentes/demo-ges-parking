package com.example.demoges_parking.utils
import java.text.SimpleDateFormat
import java.util.*

fun obtenerFechaActual(): String {
    val formato = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH)
    return formato.format(Date()) // Devuelve algo como: 14-07-2025 05:20 PM
}
