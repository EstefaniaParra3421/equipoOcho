package com.univalle.inventorywidget

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.univalle.inventorywidget.ui.auth.FirebaseLoginScreen
import com.univalle.inventorywidget.ui.theme.InventoryWidgetTheme
import com.univalle.inventorywidget.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity de login que soporta:
 * - Autenticación biométrica (huella/rostro)
 * - Autenticación con Firebase (email/contraseña)
 */
@AndroidEntryPoint
class LoginActivityWithFirebase : FragmentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si ya hay una sesión activa
        authViewModel.checkSession()
        val currentEmail = authViewModel.getCurrentUserEmail()
        
        if (currentEmail != null) {
            // Si hay sesión activa, ir directamente a Home
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        // Capturar referencia de la Activity para usar en lambdas
        val activity = this
        
        setContent {
            InventoryWidgetTheme {
                var showFirebaseLogin by remember { mutableStateOf(false) }
                
                if (showFirebaseLogin) {
                    // Mostrar pantalla de login con Firebase
                    FirebaseLoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { email ->
                            activity.startActivity(Intent(activity, HomeActivity::class.java))
                            activity.finish()
                        }
                    )
                } else {
                    // Mostrar pantalla de login biométrico
                    BiometricLoginScreen(
                        onAuthenticated = {
                            activity.startActivity(Intent(activity, HomeActivity::class.java))
                            activity.finish()
                        },
                        onUseEmailPassword = {
                            showFirebaseLogin = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BiometricLoginScreen(
    onAuthenticated: () -> Unit,
    onUseEmailPassword: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E2328)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            // --- Logo + texto alineados ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.inventory),
                        contentDescription = "Logo Inventory",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(end = 20.dp)
                    )
                }

                val fredoka = FontFamily(Font(R.font.fredoka_one))

                Text(
                    text = "Inventory",
                    fontFamily = fredoka,
                    color = Color(0xFFFF7B00),
                    fontSize = 100.sp,
                    modifier = Modifier.offset(y = (-80).dp)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            // --- Imagen de huella digital ---
            Image(
                painter = painterResource(id = R.drawable.fingerprint_image),
                contentDescription = "Huella digital",
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        val activity = context as? FragmentActivity
                        if (activity == null) {
                            Toast
                                .makeText(context, "No se pudo iniciar autenticación", Toast.LENGTH_SHORT)
                                .show()
                            return@clickable
                        }

                        val biometricManager = BiometricManager.from(activity)
                        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                            BiometricManager.BIOMETRIC_SUCCESS -> {
                                val executor = ContextCompat.getMainExecutor(activity)
                                val promptInfo = BiometricPrompt.PromptInfo
                                    .Builder()
                                    .setTitle("Autenticación biométrica")
                                    .setSubtitle("Ingrese su huella digital")
                                    .setNegativeButtonText("Cancelar")
                                    .build()

                                val biometricPrompt = BiometricPrompt(
                                    activity,
                                    executor,
                                    object : BiometricPrompt.AuthenticationCallback() {
                                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                            super.onAuthenticationSucceeded(result)
                                            onAuthenticated()
                                        }

                                        override fun onAuthenticationError(
                                            errorCode: Int,
                                            errString: CharSequence
                                        ) {
                                            super.onAuthenticationError(errorCode, errString)
                                            Toast
                                                .makeText(context, "Error: $errString", Toast.LENGTH_SHORT)
                                                .show()
                                        }

                                        override fun onAuthenticationFailed() {
                                            super.onAuthenticationFailed()
                                            Toast
                                                .makeText(context, "Huella no reconocida", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                )

                                biometricPrompt.authenticate(promptInfo)
                            }

                            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                                Toast
                                    .makeText(context, "No hay hardware biométrico.", Toast.LENGTH_SHORT)
                                    .show()

                            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                                Toast
                                    .makeText(
                                        context,
                                        "Hardware biométrico no disponible.",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()

                            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                                Toast
                                    .makeText(context, "No hay huellas registradas.", Toast.LENGTH_SHORT)
                                    .show()

                            else ->
                                Toast
                                    .makeText(context, "Autenticación no disponible.", Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón para usar email/contraseña
            Text(
                text = "Iniciar sesión con email",
                color = Color(0xFFFF7B00),
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable { onUseEmailPassword() }
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

