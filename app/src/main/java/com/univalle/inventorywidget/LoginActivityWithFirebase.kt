package com.univalle.inventorywidget

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.fragment.app.FragmentActivity
import com.univalle.inventorywidget.ui.auth.FirebaseLoginScreen
import com.univalle.inventorywidget.ui.theme.InventoryWidgetTheme
import com.univalle.inventorywidget.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity de login con Firebase (email/contraseña)
 */
@AndroidEntryPoint
class LoginActivityWithFirebase : FragmentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Criterio 10 y 13: Leer extras del Intent para saber el destino después del login
        val returnTo = intent.getStringExtra("RETURN_TO")
        val showBalance = intent.getBooleanExtra("SHOW_BALANCE", false)
        val shouldReturnToWidget = returnTo == "WIDGET" && showBalance
        // Si RETURN_TO == "HOME_INVENTORY", después del login va a HomeActivity (HU 3.0)

        // Verificar si ya hay una sesión activa
        authViewModel.checkSession()

        // Capturar referencia de la Activity para usar en lambdas
        val activity = this
        
        setContent {
            InventoryWidgetTheme {
                // Observar si hay sesión activa
                val currentEmail by authViewModel.currentUserEmail.observeAsState()
                
                // Si hay sesión activa, manejar según el destino
                LaunchedEffect(currentEmail) {
                    if (currentEmail != null) {
                        if (shouldReturnToWidget) {
                            // Criterio 10: Volver al widget y mostrar el saldo
                            val widgetIntent = Intent(activity, InventoryWidgetProvider::class.java).apply {
                                action = InventoryWidgetProvider.ACTION_SHOW_BALANCE_AFTER_LOGIN
                            }
                            activity.sendBroadcast(widgetIntent)
                            activity.finish() // Cerrar y volver al widget
                        } else {
                            // Ir a Home normalmente
                            activity.startActivity(Intent(activity, HomeActivity::class.java))
                            activity.finish()
                        }
                    }
                }
                
                // Mostrar pantalla de login con Firebase
                FirebaseLoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { email ->
                        if (shouldReturnToWidget) {
                            // Criterio 10: Volver al widget y mostrar el saldo
                            val widgetIntent = Intent(activity, InventoryWidgetProvider::class.java).apply {
                                action = InventoryWidgetProvider.ACTION_SHOW_BALANCE_AFTER_LOGIN
                            }
                            activity.sendBroadcast(widgetIntent)
                            activity.finish() // Cerrar y volver al widget
                        } else {
                            // Ir a Home normalmente
                            activity.startActivity(Intent(activity, HomeActivity::class.java))
                            activity.finish()
                        }
                    }
                )
            }
        }
    }
}

