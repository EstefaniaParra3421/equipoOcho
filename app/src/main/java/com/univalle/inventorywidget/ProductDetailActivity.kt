package com.univalle.inventorywidget

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.ui.theme.InventoryWidgetTheme
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val productId = intent.getIntExtra("product_id", -1)
        val productName = intent.getStringExtra("product_name") ?: ""
        val productCategory = intent.getStringExtra("product_category") ?: ""
        val productQuantity = intent.getIntExtra("product_quantity", 0)
        val productPrice = intent.getDoubleExtra("product_price", 0.0)
        
        setContent {
            InventoryWidgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF000000) // Fondo negro (#CC000000 con opacidad completa)
                ) {
                    val viewModel: ProductViewModel = viewModel()
                    ProductDetailScreen(
                        productId = productId,
                        productName = productName,
                        productCategory = productCategory,
                        productQuantity = productQuantity,
                        productPrice = productPrice,
                        viewModel = viewModel,
                        onBack = { finish() },
                        onEdit = {
                            // TODO: Navegar a HU 6.0 (EditProductActivity)
                            Toast.makeText(this@ProductDetailActivity, "Funcionalidad editar producto próximamente (HU 6.0)", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    productName: String,
    productCategory: String,
    productQuantity: Int,
    productPrice: Double,
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Calcular total (precio x cantidad)
    val total = productPrice * productQuantity
    
    // Formatear números con separadores de miles
    val numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"))
    numberFormat.maximumFractionDigits = 2
    
    val formattedPrice = numberFormat.format(productPrice)
    val formattedTotal = numberFormat.format(total)
    
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
                IconButton(onClick = onBack) {
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
                        text = productName,
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    HorizontalDivider(color = Color.LightGray)
                    
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
                    
                    HorizontalDivider(color = Color.LightGray)
                    
                    // Cantidad
                    Text(
                        text = "Cantidad:",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = productQuantity.toString(),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    HorizontalDivider(color = Color.LightGray)
                    
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
        
        // Ícono flotante naranja (inferior derecho) para editar
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onEdit,
                containerColor = Color(0xFFFF7B00), // Naranja según criterio
                contentColor = Color.White,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar producto"
                )
            }
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
                        // Crear objeto Product para eliminar (solo necesitamos el id)
                        val product = Product(
                            id = productId,
                            code = 0, // No se usa para eliminar
                            name = productName,
                            price = productPrice,
                            quantity = productQuantity
                        )
                        //comentado porque ya no se usa room agregarf metodos al firestone
                        //viewModel.deleteProduct(product)
                        Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = false
                        onBack()
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

