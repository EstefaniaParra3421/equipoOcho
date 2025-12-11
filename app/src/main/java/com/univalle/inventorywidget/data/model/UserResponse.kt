package com.univalle.inventorywidget.data.model

/**
 * Modelo de datos para respuestas de usuario (registro/login)
 */
data class UserResponse(
    val email: String? = null,
    val isRegister: Boolean = false,
    val message: String = ""
)

