package com.univalle.inventorywidget.data.model

/**
 * Modelo de datos para solicitudes de usuario (registro/login)
 */
data class UserRequest(
    val email: String,
    val password: String
)

