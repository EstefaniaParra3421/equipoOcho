package com.univalle.inventorywidget.data.repository

import com.univalle.inventorywidget.data.model.UserRequest
import com.univalle.inventorywidget.data.model.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar la autenticación con Firebase
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    /**
     * Registra un nuevo usuario en Firebase Authentication
     */
    suspend fun registerUser(userRequest: UserRequest, userResponse: (UserResponse) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuth.createUserWithEmailAndPassword(userRequest.email, userRequest.password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val email = task.result?.user?.email
                            userResponse(
                                UserResponse(
                                    email = email,
                                    isRegister = true,
                                    message = "Registro Exitoso"
                                )
                            )
                        } else {
                            val error = task.exception
                            if (error is FirebaseAuthUserCollisionException) {
                                userResponse(
                                    UserResponse(
                                        isRegister = false,
                                        message = "El usuario ya existe"
                                    )
                                )
                            } else {
                                userResponse(
                                    UserResponse(
                                        isRegister = false,
                                        message = "Error en el registro"
                                    )
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                userResponse(
                    UserResponse(
                        isRegister = false,
                        message = e.message ?: "Error desconocido"
                    )
                )
            }
        }
    }

    /**
     * Inicia sesión con un usuario existente
     */
    fun loginUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, task.result?.user?.email)
                    } else {
                        callback(false, task.exception?.message)
                    }
                }
        } else {
            callback(false, "Email y contraseña no pueden estar vacíos")
        }
    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun logOut() {
        firebaseAuth.signOut()
    }

    /**
     * Obtiene el email del usuario actual
     */
    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    /**
     * Verifica si hay una sesión activa
     */
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}

