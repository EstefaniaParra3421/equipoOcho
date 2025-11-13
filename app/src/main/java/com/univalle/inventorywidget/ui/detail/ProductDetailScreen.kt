package com.univalle.inventorywidget.ui.detail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    navController: NavController,
    viewModel: ProductViewModel
) {
    val products by viewModel.allProducts.observeAsState(initial = null)
    val product = products?.find { it.id == productId }
    val context = LocalContext.current
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    if (product == null) {
        // Producto no encontrado o aún cargando
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFF9800))
        }
        return
    }
    
    // Calcular total (precio x cantidad)
    val total = product.price * product.quantity
    
    // Formatear números con separadores de miles
    val numberFormat = NumberFormat.getNumberInstance(Locale("es", "CO"))
    numberFormat.maximumFractionDigits = 2
    
    val formattedPrice = numberFormat.format(product.price)
    val formattedTotal = numberFormat.format(total)
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF000000) // Fondo negro
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Toolbar gris (#424242) con texto blanco "Detalle del producto" y flecha izquierda
                TopAppBar(
                    title = {
                        Text(
                            text = "Detalle del producto",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF424242) // Gris según criterio
                    )
                )
                
                // Contenido principal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Tarjeta blanca con bordes redondeados
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Nombre
                            Text(
                                text = "Nombre:",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = product.name,
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Divider(color = Color.LightGray)
                            
                            // Precio
                            Text(
                                text = "Precio:",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$$formattedPrice",
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Divider(color = Color.LightGray)
                            
                            // Cantidad
                            Text(
                                text = "Cantidad:",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = product.quantity.toString(),
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Divider(color = Color.LightGray)
                            
                            // Total (precio x cantidad)
                            Text(
                                text = "Total:",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$$formattedTotal",
                                color = Color(0xFFFF7B00), // Naranja para destacar
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Botón naranja "Eliminar"
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7B00) // Naranja según criterio
                        )
                    ) {
                        Text(
                            text = "Eliminar",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            
            // Ícono flotante naranja (inferior derecho) para editar
            FloatingActionButton(
                onClick = {
                    // TODO: Navegar a HU 6.0 (EditProductScreen)
                    // Por ahora no hace nada, solo está preparado para la funcionalidad futura
                },
                containerColor = Color(0xFFFF7B00), // Naranja según criterio
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar producto"
                )
            }
        }
    
    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Confirmar eliminación",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Está seguro de que desea eliminar este producto?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product)
                        Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7B00)
                    )
                ) {
                    Text("Sí", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
    }
}

