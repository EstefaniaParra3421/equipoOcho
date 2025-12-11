package com.univalle.inventorywidget.domain.usecase

import com.univalle.inventorywidget.data.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Boolean {
        return repository.login(email, password)
    }
}
