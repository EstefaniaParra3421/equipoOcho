package com.univalle.inventorywidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.univalle.inventorywidget.ui.addproduct.AddProductScreen
import com.univalle.inventorywidget.ui.detail.ProductDetailScreen
import com.univalle.inventorywidget.ui.editproduct.EditProductScreen
import com.univalle.inventorywidget.ui.home.HomeInventoryScreen
import com.univalle.inventorywidget.ui.theme.InventoryWidgetTheme
import com.univalle.inventorywidget.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryWidgetTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = Color(0xFF1E2328)
                ) {
                    InventoryNavHost()
                }
            }
        }
    }
}

@Composable
fun InventoryNavHost() {
    val navController: NavHostController = rememberNavController()

    val sharedViewModel: ProductViewModel = hiltViewModel()

    BackHandler {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    NavHost(
        navController = navController,
        startDestination = "home_inventory_screen"
    ) {
        // HU 3.0: Home Inventario
        composable("home_inventory_screen") {
            HomeInventoryScreen(
                navController = navController,
                viewModel = sharedViewModel
            )
        }

        // HU 4.0: Agregar Producto
        composable("add_product_screen") {
            AddProductScreen(
                navController = navController,
                viewModel = sharedViewModel
            )
        }

        // HU 5.0: Detalle del Producto
        composable(
            route = "product_detail_screen/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
            ProductDetailScreen(
                productId = productId,
                navController = navController,
                viewModel = sharedViewModel
            )
        }

        // HU 6.0: Editar Producto
        composable(
            route = "edit_product_screen/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
            EditProductScreen(
                productId = productId,
                navController = navController,
                viewModel = sharedViewModel
            )
        }
    }
}
