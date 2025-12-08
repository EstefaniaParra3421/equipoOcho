package com.univalle.inventorywidget.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.univalle.inventorywidget.HomeActivity
import com.univalle.inventorywidget.LoginActivity
import com.univalle.inventorywidget.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeInventoryScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    //comentado porque ya no existen metodos en el viewmodel agregarlos usando firestone
    //val products by viewModel.allProducts.observeAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario", color = Color.White) },
                actions = {
                    IconButton(onClick = {
                        val context = navController.context
                        val activity = context as? HomeActivity

                        // Borra autenticación
                        val prefs = activity?.getSharedPreferences("auth", Context.MODE_PRIVATE)
                        prefs?.edit()?.putBoolean("authenticated", false)?.apply()

                        // Ir al LoginActivity
                        val intent = Intent(activity, LoginActivity::class.java)
                        activity?.startActivity(intent)
                        activity?.finish()
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
            /*when {
                products == null -> {
                    // Aún cargando
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF9800))
                    }
                }
                products?.isEmpty() == true -> {
                    // Sin productos
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "No hay productos",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Presiona el botón + para crear un nuevo producto",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                else -> {
                    products?.let { productList ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(productList) { product ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            navController.navigate("product_detail_screen/${product.id}")
                                        },
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
            }*/
        }
    }
}
