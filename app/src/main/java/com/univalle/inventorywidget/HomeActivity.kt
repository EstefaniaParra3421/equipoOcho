package com.univalle.inventorywidget

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.inventorywidget.ui.theme.InventoryWidgetTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryWidgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1E2328)
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val quantity: Int
)

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // Lista base de ejemplo
    val productList = remember {
        listOf(
            Product(1, "Laptop Lenovo ThinkPad", "Computador", 5),
            Product(2, "Mouse Logitech G203", "Periférico", 12),
            Product(3, "Monitor Samsung 24\"", "Pantalla", 8)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Título principal
            Text(
                text = "Inventario",
                color = Color(0xFFFF7B00),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 40.dp, start = 20.dp, bottom = 20.dp)
            )

            // Lista de productos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productList) { product ->
                    ProductCard(product = product) {
                        Toast.makeText(
                            context,
                            "Producto: ${product.name}\nCantidad: ${product.quantity}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Botón flotante “Agregar producto”
        FloatingActionButton(
            onClick = {
                Toast.makeText(context, "Funcionalidad agregar producto próximamente", Toast.LENGTH_SHORT).show()
            },
            containerColor = Color(0xFFFF7B00),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar producto")
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C343A)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Categoría: ${product.category}",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            Text(
                text = "Cantidad: ${product.quantity}",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}