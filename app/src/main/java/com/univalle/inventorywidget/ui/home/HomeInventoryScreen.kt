package com.univalle.inventorywidget.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.univalle.inventorywidget.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeInventoryScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    val products by viewModel.allProducts.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario", color = Color.White) },
                actions = {
                    IconButton(onClick = {
                        // navController.navigate("login_screen") { popUpTo(0) }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF424242))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_product_screen")
                },
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ) {
                Text("+")
            }
        },
        containerColor = Color(0xCC000000)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xCC000000))
        ) {
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF9800))
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
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(product.name, color = Color.Black)
                                    Text("ID: ${product.id}", color = Color.Gray)
                                }
                                Text(
                                    text = "$ ${product.price}",
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
