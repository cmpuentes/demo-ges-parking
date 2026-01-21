package com.gesnnova.demoges_parking.model

data class GenericResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
