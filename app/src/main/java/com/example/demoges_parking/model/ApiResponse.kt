package com.example.demoges_parking.model

data class ApiResponse(
    val success: Boolean,
    val message: String?,
    val data: Map<String, Any>? = null
)
