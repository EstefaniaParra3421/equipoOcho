package com.univalle.inventorywidget.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.domain.usecase.LoginUseCase
import com.univalle.inventorywidget.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> = _loginResult

    private val _registerResult = MutableStateFlow<Boolean?>(null)
    val registerResult: StateFlow<Boolean?> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = loginUseCase(email, password)
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = registerUseCase(email, password)
        }
    }
}
