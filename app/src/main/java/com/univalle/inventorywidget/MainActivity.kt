package com.univalle.inventorywidget

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.fragment.app.FragmentActivity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.univalle.inventorywidget.ui.addproduct.AddProductScreen

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryApp()
        }
    }
}

@Composable
fun InventoryApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main_screen"
    ) {
        composable("main_screen") {
            MainScreen(onAddProductClick = {
                navController.navigate("add_product_screen")
            })
        }

        composable("add_product_screen") {
            AddProductScreen(navController = navController)
        }
    }
}

@Composable
fun MainScreen(onAddProductClick: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = Color(0xFFFF9800)
            ) {
                Text("+", color = Color.White)
            }
        },
        containerColor = Color(0xFF202020)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF202020)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Bienvenido al inventario", color = Color.White)
        }
    }
}
