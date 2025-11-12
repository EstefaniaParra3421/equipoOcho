package com.univalle.inventorywidget.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.univalle.inventorywidget.viewmodel.ProductViewModel

@Composable
fun HomeInventoryScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.allProducts.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF424242))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_product_screen") },
                containerColor = Color(0xFFFF9800)
            ) {
                Text("+", color = Color.White)
            }
        },
        containerColor = Color(0xFF202020)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF202020))
        ) {
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos guardados", color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(products) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF424242))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Código: ${product.code}", color = Color.White)
                                Text("Nombre: ${product.name}", color = Color.White)
                                Text("Precio: ${product.price}", color = Color.White)
                                Text("Cantidad: ${product.quantity}", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
