package com.univalle.inventorywidget.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.data.model.UserRequest
import com.univalle.inventorywidget.viewmodel.AuthViewModel

/**
 * Pantalla de login con Firebase Authentication
 * Permite login con email/password y registro de nuevos usuarios
 */
@Composable
fun FirebaseLoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val loginResult by authViewModel.loginResult.observeAsState()
    val registerResult by authViewModel.registerResult.observeAsState()

    // Observar resultados del login
    LaunchedEffect(loginResult) {
        loginResult?.let { (success, message) ->
            if (success) {
                Toast.makeText(context, "Login exitoso", Toast.LENGTH_SHORT).show()
                message?.let { onLoginSuccess(it) }
            } else {
                Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Observar resultados del registro
    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            if (result.isRegister) {
                result.email?.let { onLoginSuccess(it) }
            }
        }
    }

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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.inventory),
                    contentDescription = "Logo Inventory",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título
            val fredoka = FontFamily(Font(R.font.fredoka_one))
            Text(
                text = "Inventory",
                fontFamily = fredoka,
                color = Color(0xFFFF7B00),
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF7B00),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF7B00)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.White) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color.White
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF7B00),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF7B00)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Login/Registro
            Button(
                onClick = {
                    if (isRegistering) {
                        authViewModel.registerUser(UserRequest(email, password))
                    } else {
                        authViewModel.loginUser(email, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7B00)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (isRegistering) "Registrarse" else "Iniciar Sesión",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto para cambiar entre Login y Registro
            Text(
                text = if (isRegistering) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate",
                color = Color(0xFFFF7B00),
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    isRegistering = !isRegistering
                }
            )
        }
    }
}

