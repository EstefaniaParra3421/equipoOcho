package com.univalle.inventorywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private var isBalanceVisible = false  // estado actual
        const val ACTION_TOGGLE_BALANCE = "TOGGLE_BALANCE"
        const val ACTION_MANAGE_INVENTORY = "MANAGE_INVENTORY"
        const val ACTION_SHOW_BALANCE_AFTER_LOGIN = "SHOW_BALANCE_AFTER_LOGIN"
    }
    
    // Scope para operaciones asíncronas
    private val widgetScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Formato de número colombiano (Criterio 9: separadores de miles y dos decimales)
    // Ejemplo: 3326000.00 → "3.326.000,00"
    private val numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO")).apply {
        minimumFractionDigits = 2  // Siempre mostrar 2 decimales
        maximumFractionDigits = 2  // Máximo 2 decimales
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Obtener FirebaseAuth directamente (AppWidgetProvider no soporta inyección de dependencias)
        val firebaseAuth = FirebaseAuth.getInstance()
        val authRepository = FirebaseAuthRepository(firebaseAuth)

        when (intent.action) {
            ACTION_TOGGLE_BALANCE -> {
                // Criterio 10: Validar login antes de mostrar saldo
                if (authRepository.isUserLoggedIn()) {
                    // Usuario está logueado, mostrar/ocultar saldo
                    isBalanceVisible = !isBalanceVisible

                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val ids = appWidgetManager.getAppWidgetIds(
                        android.content.ComponentName(context, InventoryWidgetProvider::class.java)
                    )

                    // actualiza todos los widgets visibles
                    for (id in ids) {
                        updateWidget(context, appWidgetManager, id)
                    }
                } else {
                    // Criterio 10: Usuario no está logueado, redirigir a HU 2.0 Login/Registro
                    // Después del login/registro debe volver al widget (no al home)
                    val loginIntent = Intent(context, LoginActivityWithFirebase::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("RETURN_TO", "WIDGET") // Indicar que debe volver al widget
                        putExtra("SHOW_BALANCE", true) // Indicar que debe mostrar el saldo
                    }
                    context.startActivity(loginIntent)
                }
            }

            ACTION_SHOW_BALANCE_AFTER_LOGIN -> {
                // Criterio 7: Mostrar el saldo después del login exitoso
                if (authRepository.isUserLoggedIn()) {
                    isBalanceVisible = true // Mostrar el saldo automáticamente

                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val ids = appWidgetManager.getAppWidgetIds(
                        android.content.ComponentName(context, InventoryWidgetProvider::class.java)
                    )

                    // actualiza todos los widgets visibles
                    for (id in ids) {
                        updateWidget(context, appWidgetManager, id)
                    }
                }
            }

            ACTION_MANAGE_INVENTORY -> {
                // Criterio 13: Validar login antes de gestionar inventario
                if (authRepository.isUserLoggedIn()) {
                    // Criterio 14: Usuario está logueado, ir a HU 3.0 Home Inventario
                    val homeIntent = Intent(context, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    context.startActivity(homeIntent)
                } else {
                    // Criterio 13: Usuario no está logueado, redirigir a HU 2.0 Login/Registro
                    // Después del login/registro debe ir a HU 3.0 Home Inventario
                    val loginIntent = Intent(context, LoginActivityWithFirebase::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("RETURN_TO", "HOME_INVENTORY") // Indicar que debe ir a Home después del login
                    }
                    context.startActivity(loginIntent)
                }
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        // Criterio 7: texto del saldo y del ícono según el estado
        if (isBalanceVisible) {
            // Calcular el saldo real del inventario desde Firestore
            widgetScope.launch {
                try {
                    val totalBalance = calculateInventoryBalance()
                    val formattedBalance = "$ ${numberFormat.format(totalBalance)}"
                    
                    // Actualizar el widget en el hilo principal
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        views.setTextViewText(R.id.txtBalance, formattedBalance)
                        views.setImageViewResource(
                            R.id.iconEye,
                            android.R.drawable.ic_menu_close_clear_cancel  // ojo cerrado
                        )
                        appWidgetManager.updateAppWidget(widgetId, views)
                    }
                } catch (e: Exception) {
                    // En caso de error, mostrar saldo por defecto
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        views.setTextViewText(R.id.txtBalance, "$ 0,00")
                        views.setImageViewResource(
                            R.id.iconEye,
                            android.R.drawable.ic_menu_close_clear_cancel  // ojo cerrado
                        )
                        appWidgetManager.updateAppWidget(widgetId, views)
                    }
                }
            }
            
            // Mientras carga, mostrar estado temporal
            views.setImageViewResource(
                R.id.iconEye,
                android.R.drawable.ic_menu_close_clear_cancel  // ojo cerrado
            )
        } else {
            views.setTextViewText(R.id.txtBalance, "$ ****")
            views.setImageViewResource(
                R.id.iconEye,
                android.R.drawable.ic_menu_view  // ojo abierto
            )
        }

        // Intent para el botón del ojo (Criterio 10)
        val eyeIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_BALANCE
        }
        val eyePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            eyeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.iconEye, eyePendingIntent)

        // Intent para gestionar inventario (Criterios 13 y 14)
        val manageIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_MANAGE_INVENTORY
        }
        val managePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            manageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.iconManage, managePendingIntent)
        views.setOnClickPendingIntent(R.id.txtManage, managePendingIntent)

        appWidgetManager.updateAppWidget(widgetId, views)
    }
    
    /**
     * Calcula el saldo total del inventario según el Criterio 8:
     * 1. Multiplica el precio por unidad del producto por la cantidad existente
     * 2. Suma todos los totales de cada producto para dar el saldo general
     * 
     * Fórmula: Saldo General = Σ (precio × cantidad) para todos los productos
     */
    private suspend fun calculateInventoryBalance(): Double {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("products").get().await()
            
            var totalBalance = 0.0
            snapshot.documents.forEach { document ->
                val data = document.data
                if (data != null) {
                    // Criterio 8: Precio por unidad × Cantidad existente
                    val price = (data["price"] as? Number)?.toDouble() ?: 0.0
                    val quantity = (data["quantity"] as? Number)?.toInt() ?: 0
                    val productTotal = price * quantity
                    
                    // Criterio 8: Sumar todos los totales de cada producto
                    totalBalance += productTotal
                }
            }
            totalBalance
        } catch (e: Exception) {
            0.0
        }
    }
}
