package com.univalle.inventorywidget.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.univalle.inventorywidget.ui.addproduct.AddProductScreen
import com.univalle.inventorywidget.ui.home.HomeInventoryScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home_screen"
    ) {
        composable("home_screen") {
            HomeInventoryScreen(navController)
        }

        composable("add_product_screen") {
            AddProductScreen(navController)
        }
    }
}
