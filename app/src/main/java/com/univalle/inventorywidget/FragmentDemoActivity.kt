package com.univalle.inventorywidget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

/**
 * Activity de demostración para mostrar el uso de Fragments y Navigation Component
 * 
 * Esta actividad implementa Navigation Component de Android siguiendo las mejores prácticas:
 * - Usa NavHostFragment como contenedor de fragmentos
 * - Configura el navigation graph para definir las rutas
 * - Implementa navegación con animaciones
 * - Permite pasar datos entre fragmentos usando Bundle
 * 
 * Estructura:
 * - HomeFragment: Pantalla principal con opciones de navegación
 * - DetailFragment: Muestra detalles de un producto
 * - AddFragment: Formulario para agregar/editar productos
 */
class FragmentDemoActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_demo)

        // Configurar el NavController
        setupNavigation()
    }

    private fun setupNavigation() {
        // Obtener el NavHostFragment del layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        
        navController = navHostFragment.navController

        // Configurar la ActionBar con el NavController
        // Esto permite mostrar el botón de retroceso automáticamente
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment) // Define los destinos de nivel superior (sin botón atrás)
        )
        
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Maneja la navegación hacia atrás cuando se presiona el botón "up" en la ActionBar
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

