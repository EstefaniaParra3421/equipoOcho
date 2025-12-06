package com.univalle.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.data.model.UserRequest
import com.univalle.inventorywidget.data.model.UserResponse
import com.univalle.inventorywidget.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la lógica de autenticación con Firebase
 */
class AuthViewModel : ViewModel() {
    private val authRepository = FirebaseAuthRepository()
    
    private val _registerResult = MutableLiveData<UserResponse>()
    val registerResult: LiveData<UserResponse> = _registerResult
    
    private val _loginResult = MutableLiveData<Pair<Boolean, String?>>()
    val loginResult: LiveData<Pair<Boolean, String?>> = _loginResult
    
    private val _currentUserEmail = MutableLiveData<String?>()
    val currentUserEmail: LiveData<String?> = _currentUserEmail

    /**
     * Registra un nuevo usuario en Firebase
     */
    fun registerUser(userRequest: UserRequest) {
        viewModelScope.launch {
            authRepository.registerUser(userRequest) { userResponse ->
                _registerResult.value = userResponse
                if (userResponse.isRegister) {
                    _currentUserEmail.value = userResponse.email
                }
            }
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    fun loginUser(email: String, password: String) {
        authRepository.loginUser(email, password) { success, emailOrError ->
            _loginResult.value = Pair(success, emailOrError)
            if (success) {
                _currentUserEmail.value = emailOrError
            }
        }
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logOut() {
        authRepository.logOut()
        _currentUserEmail.value = null
    }

    /**
     * Verifica si hay una sesión activa
     */
    fun checkSession() {
        if (authRepository.isUserLoggedIn()) {
            _currentUserEmail.value = authRepository.getCurrentUserEmail()
        }
    }

    /**
     * Obtiene el email del usuario actual
     */
    fun getCurrentUserEmail(): String? {
        return authRepository.getCurrentUserEmail()
    }
}

