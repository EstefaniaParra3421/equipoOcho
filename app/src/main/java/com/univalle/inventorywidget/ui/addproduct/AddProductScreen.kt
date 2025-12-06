package com.univalle.inventorywidget.ui.addproduct

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    val isButtonEnabled =
        code.isNotBlank() && name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar producto", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF424242)
                )
            )
        },
        containerColor = Color(0xCC000000)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo Código del producto
            OutlinedTextField(
                value = code,
                onValueChange = {
                    if (it.length <= 4 && it.all(Char::isDigit)) code = it
                },
                label = { Text("Código producto", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Nombre del artículo
            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 40) name = it
                },
                label = { Text("Nombre artículo", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Precio
            OutlinedTextField(
                value = price,
                onValueChange = {
                    if (it.length <= 20 && it.all(Char::isDigit)) price = it
                },
                label = { Text("Precio", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Cantidad
            OutlinedTextField(
                value = quantity,
                onValueChange = {
                    if (it.length <= 4 && it.all(Char::isDigit)) quantity = it
                },
                label = { Text("Cantidad", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Guardar
            Button(
                onClick = {
                    val newProduct = Product(
                        code = code.toInt(),
                        name = name,
                        price = price.toDouble(),
                        quantity = quantity.toInt()
                    )
                    viewModel.insertProduct(newProduct)
                    navController.popBackStack()
                },
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    disabledContainerColor = Color.Gray
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Guardar",
                    color = Color.White,
                    fontWeight = if (isButtonEnabled) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
