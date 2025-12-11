package com.univalle.inventorywidget.data.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(email: String, password: String): Boolean
    fun isUserLoggedIn(): Boolean
}
