package com.univalle.inventorywidget.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.inventorywidget.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    // Observa resultados del ViewModel (StateFlow en ViewModel)
    val loginResult by viewModel.loginResult.collectAsState(initial = null)
    val registerResult by viewModel.registerResult.collectAsState(initial = null)

    // Estados locales para los campos
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados de foco (para UI si quieres agregar lógica extra)
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    // Derivado: validaciones
    val isEmailFilled = email.isNotBlank()
    val emailMax = 40
    val filteredEmail = if (email.length <= emailMax) email else email.take(emailMax)

    // Password: solo dígitos, min 6, max 10
    val filteredPassword = remember(password) {
        // Filtrar solo dígitos y limitar longitud a 10
        password.filter { it.isDigit() }.take(10)
    }

    // Si el usuario está tipeando, mantener el valor filtrado
    LaunchedEffect(filteredPassword) {
        if (filteredPassword != password) password = filteredPassword
    }

    val passwordLength = password.length
    val isPasswordMin = passwordLength >= 6
    val areFieldsFilled = isEmailFilled && password.isNotEmpty() && isPasswordMin

    // Muestra Toasts según el resultado del ViewModel
    LaunchedEffect(loginResult) {
        when (loginResult) {
            true -> onLoginSuccess()
            false -> {
                Toast.makeText(context, "Login incorrecto", Toast.LENGTH_SHORT).show()
                // reset state in ViewModel handled there if needed
            }
            else -> { /* null -> no action */ }
        }
    }

    LaunchedEffect(registerResult) {
        when (registerResult) {
            true -> onLoginSuccess() // registro exitoso, ir a Home
            false -> {
                Toast.makeText(context, "Error en el registro", Toast.LENGTH_SHORT).show()
            }
            else -> { }
        }
    }

    // UI principal
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- Logo en la parte superior ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.mipmap.inventory_logo),
                    contentDescription = "Logo Inventory",
                    modifier = Modifier
                        .size(160.dp)
                )
            }

            // --- Campos (centrados) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email - OutlinedTextField con label blanco y borde que resalta en foco
                OutlinedTextField(
                    value = filteredEmail,
                    onValueChange = { new ->
                        // limitar a 40 caracteres
                        email = if (new.length <= emailMax) new else new.take(emailMax)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { state -> emailFocused = state.isFocused },
                    label = { Text(text = "Email", color = Color.White) },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                        cursorColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password - solo números, ojo (icono a la izquierda)
                OutlinedTextField(
                    value = password,
                    onValueChange = { new ->
                        // filtrar dígitos y limitar a 10
                        password = new.filter { it.isDigit() }.take(10)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { state -> passwordFocused = state.isFocused },
                    label = { Text(text = "Password", color = Color.White) },
                    singleLine = true,
                    leadingIcon = {
                        // Ícono de ojo (izquierda) que cambia visibilidad
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            if (passwordVisible) {
                                Icon(
                                    painter = painterResource(id = R.drawable.fingerprint_image),
                                    contentDescription = "Ocultar contraseña",
                                    tint = Color.White
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.fingerprint_image),
                                    contentDescription = "Mostrar contraseña",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = TextStyle(color = Color.White, letterSpacing = 2.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (!isPasswordMin && passwordLength > 0) Color.Red else Color.White,
                        unfocusedBorderColor = if (!isPasswordMin && passwordLength > 0) Color.Red else Color.White.copy(alpha = 0.6f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                        cursorColor = Color.White
                    )
                )

                // Mensaje de error en tiempo real si < 6 dígitos
                if (passwordLength in 1..5) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Mínimo 6 dígitos",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(18.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Botón Login (naranja, redondeado)
                Button(
                    onClick = {
                        // llamar al ViewModel para login
                        viewModel.login(email.trim(), password)
                    },
                    enabled = areFieldsFilled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (areFieldsFilled) Color(0xFFFF7B00) else Color(0xFFB55A00)
                    )
                ) {
                    Text(
                        text = "Login",
                        color = if (areFieldsFilled) Color.White else Color.White.copy(alpha = 0.9f),
                        style = if (areFieldsFilled) LocalTextStyle.current.copy(fontWeight = FontWeight.Bold) else LocalTextStyle.current
                    )
                }
            }

            // --- Registrarse (texto botón) en la parte inferior ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registrarse",
                    color = if (areFieldsFilled) Color.White else Color(0xFF9EA1A1),
                    fontWeight = if (areFieldsFilled) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clickable(enabled = areFieldsFilled) {
                            // llamar register
                            viewModel.register(email.trim(), password)
                        }
                        .padding(vertical = 18.dp)
                )
            }
        }
    }
}
