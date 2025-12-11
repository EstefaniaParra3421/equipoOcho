package com.univalle.inventorywidget.ui.addproduct

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: ProductViewModel = hiltViewModel()
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    val isButtonEnabled =
        code.length == 4 &&
                name.isNotBlank() &&
                price.isNotBlank() &&
                quantity.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar producto", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF424242)
                )
            )
        },
        containerColor = Color(0xCC000000) // Fondo solicitado
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo Código
            OutlinedTextField(
                value = code,
                onValueChange = {
                    if (it.length <= 4 && it.all(Char::isDigit)) code = it
                },
                label = { Text("Código producto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            // Campo Nombre
            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 40) name = it
                },
                label = { Text("Nombre artículo") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            // Campo Precio
            OutlinedTextField(
                value = price,
                onValueChange = {
                    if (it.length <= 20 && it.all(Char::isDigit)) price = it
                },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            // Campo Cantidad
            OutlinedTextField(
                value = quantity,
                onValueChange = {
                    if (it.length <= 4 && it.all(Char::isDigit)) quantity = it
                },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(Modifier.height(24.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    val p = Product(
                        code = code.toInt(),
                        name = name,
                        price = price.toDouble(),
                        quantity = quantity.toInt()
                    )

                    viewModel.insertProduct(p)
                    navController.popBackStack() // vuelve a HU 3.0
                },
                enabled = isButtonEnabled,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF3C00),
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(
                    "Guardar",
                    color = Color.White,
                    fontWeight = if (isButtonEnabled) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun textFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.White,
    unfocusedIndicatorColor = Color.Gray,
    cursorColor = Color.White,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White
)
