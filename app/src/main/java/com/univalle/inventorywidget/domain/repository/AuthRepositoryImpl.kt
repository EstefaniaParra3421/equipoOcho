package com.univalle.inventorywidget.domain.repository

import com.univalle.inventorywidget.data.datasource.FirebaseAuthDataSource
import com.univalle.inventorywidget.data.repository.AuthRepository

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String) =
        firebaseAuthDataSource.login(email, password)

    override suspend fun register(email: String, password: String) =
        firebaseAuthDataSource.register(email, password)

    override fun isUserLoggedIn() =
        firebaseAuthDataSource.isUserLoggedIn()
}

