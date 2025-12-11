package com.univalle.inventorywidget.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.univalle.inventorywidget.HomeActivity
import com.univalle.inventorywidget.LoginActivityWithFirebase
import com.univalle.inventorywidget.viewmodel.AuthViewModel
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeInventoryScreen(
    navController: NavController,
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // Observar productos desde el ViewModel (StateFlow)
    val products by viewModel.products.collectAsState()
    
    // Estado de carga
    var isLoading by remember { mutableStateOf(true) }
    
    // Cargar productos al iniciar
    LaunchedEffect(Unit) {
        isLoading = true
        viewModel.loadProducts()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario", color = Color.White) },
                actions = {
                    IconButton(onClick = {
                        // Cerrar sesión en Firebase
                        authViewModel.logOut()
                        
                        // Navegar a la pantalla de login
                        val intent = Intent(context, LoginActivityWithFirebase::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        
                        // Finalizar la actividad actual
                        (context as? HomeActivity)?.finish()
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión", tint = Color.White)
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
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar producto",
                    tint = Color.White
                )
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
            when {
                isLoading -> {
                    // Mostrando progress circular naranja mientras carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF9800),
                            strokeWidth = 4.dp
                        )
                    }
                }
                products.isEmpty() -> {
                    // Sin productos después de cargar
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
                    run {
                        val productList = products
                        // Formato de número colombiano (HU 1.0)
                        val numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"))
                        numberFormat.minimumFractionDigits = 2
                        numberFormat.maximumFractionDigits = 2
                        
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(productList) { product ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            navController.navigate("product_detail_screen/${product.id}")
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Columna izquierda: Nombre del producto (arriba) e ID (abajo)
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = product.name,
                                                color = Color.Black,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "ID: ${product.id}",
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                        }
                                        
                                        // Precio en la parte central derecha (naranja)
                                        Text(
                                            text = "$ ${numberFormat.format(product.price)}",
                                            color = Color(0xFFFF9800),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
