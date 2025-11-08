package com.univalle.inventorywidget

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.univalle.inventorywidget.ui.theme.InventoryWidgetTheme

class LoginActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InventoryWidgetTheme {
                LoginScreen(onAuthenticated = {
                    // Navegar a la actividad principal cuando la autenticación es exitosa
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                })
            }
        }
    }
}

@Composable
fun LoginScreen(onAuthenticated: () -> Unit) {
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
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Inventory", color = Color(0xFFFF7B00))

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                val activity = context as? FragmentActivity
                if (activity == null) {
                    Toast.makeText(context, "No se pudo iniciar autenticación", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val biometricManager = BiometricManager.from(activity)
                when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        val executor = ContextCompat.getMainExecutor(activity)
                        val promptInfo = BiometricPrompt.PromptInfo.Builder()
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

                                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                    super.onAuthenticationError(errorCode, errString)
                                    Toast.makeText(context, "Error: $errString", Toast.LENGTH_SHORT).show()
                                }

                                override fun onAuthenticationFailed() {
                                    super.onAuthenticationFailed()
                                    Toast.makeText(context, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        biometricPrompt.authenticate(promptInfo)
                    }

                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                        Toast.makeText(context, "No hay hardware biométrico.", Toast.LENGTH_SHORT).show()
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                        Toast.makeText(context, "Hardware biométrico no disponible.", Toast.LENGTH_SHORT).show()
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                        Toast.makeText(context, "No hay huellas registradas.", Toast.LENGTH_SHORT).show()
                    else ->
                        Toast.makeText(context, "Autenticación no disponible.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Usar huella")
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
