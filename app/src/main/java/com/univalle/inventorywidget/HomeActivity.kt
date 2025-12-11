package com.univalle.inventorywidget

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.univalle.inventorywidget.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal que gestiona la navegación del inventario
 * HU 3.0: Ventana Home Inventario
 * 
 * Implementa Navigation Component con Fragments:
 * - HomeInventoryFragment: Lista de productos (pantalla principal)
 * - ProductDetailFragment: Detalle del producto (HU 5.0)
 * - AddProductFragment: Agregar producto (HU 4.0)
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Criterio 1: Verificar si hay una sesión activa
        authViewModel.checkSession()
        
        // Criterio 3: Si no hay sesión, redirigir al login
        if (authViewModel.getCurrentUserEmail() == null) {
            val intent = Intent(this, LoginActivityWithFirebase::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
        
        setContentView(R.layout.activity_home)
        
        // Configurar el status bar con el mismo color que el toolbar (#424242)
        setupStatusBar()
        
        setupNavigation()
    }
    
    /**
     * Configura el status bar de manera moderna sin deprecation warnings
     */
    private fun setupStatusBar() {
        // Permitir que el contenido se extienda debajo del status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Configurar color del status bar (#424242) igual que el toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @Suppress("DEPRECATION")
            window.statusBarColor = 0xFF424242.toInt()
        }
        
        // Asegurar que los iconos del status bar sean blancos (light status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = 
                window.decorView.systemUiVisibility or android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            // Pero como el fondo es oscuro, queremos iconos blancos, así que no usamos LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = 
                window.decorView.systemUiVisibility and android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
    
    /**
     * Configura el NavController con el NavHostFragment
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        
        navController = navHostFragment.navController
    }
    
    /**
     * Maneja la navegación hacia atrás cuando se presiona el botón "up"
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
