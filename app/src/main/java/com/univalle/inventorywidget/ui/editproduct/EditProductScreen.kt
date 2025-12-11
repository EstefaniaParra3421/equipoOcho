package com.univalle.inventorywidget.ui.editproduct

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: Int,
    navController: NavController,
    viewModel: ProductViewModel
) {
    //comentado porque ya no se usa room (adaptar a firestone)
    //val products by viewModel.allProducts.observeAsState(initial = null)
    //val product = products?.find { it.id == productId }
    val context = LocalContext.current

    // Estados para los campos editables
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    // Cargar datos del producto cuando esté disponible
    /*LaunchedEffect(product) {
        if (product != null) {
            name = product.name
            price = product.price.toString()
            quantity = product.quantity.toString()
        }
    }*/

    // Validar que todos los campos estén llenos
    val isButtonEnabled = name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()

    /*if (product == null) {
        // Producto no encontrado o aún cargando
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFF9800))
        }
        return
    }*/

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xCC000000) // Fondo negro según criterio
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Editar producto",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF424242) // Gris según criterio
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campo ID del producto (no editable)
                OutlinedTextField(
                    value = "",//product.id.toString(),
                    onValueChange = {}, // No editable
                    label = { Text("ID del producto", color = Color.White) },
                    enabled = false, // Deshabilitado
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color(0xFF2C2C2C),
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.Gray,
                        disabledIndicatorColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        disabledTextColor = Color.Gray,
                        disabledLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Campo Nombre del artículo (máximo 40 caracteres)
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
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Campo Precio (máximo 20 dígitos numéricos)
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        // Solo números y un punto decimal, máximo 20 caracteres
                        val filtered = it.filter { char -> char.isDigit() || char == '.' }
                        // Evitar múltiples puntos decimales
                        if (filtered.count { it == '.' } <= 1 && filtered.length <= 20) {
                            price = filtered
                        }
                    },
                    label = { Text("Precio", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Campo Cantidad (máximo 4 dígitos numéricos)
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
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón naranja "Editar"
                Button(
                    onClick = {
                        try {
                            val updatedProduct = Product(
                                id = 0,//product.id,
                                code = 0,//product.code,
                                name = name.trim(),
                                price = price.toDouble(),
                                quantity = quantity.toInt()
                            )
                            //viewModel.updateProduct(updatedProduct)
                            Toast.makeText(context, "Producto actualizado", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al actualizar producto", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = isButtonEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7B00), // Naranja según criterio
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "Editar",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = if (isButtonEnabled) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

