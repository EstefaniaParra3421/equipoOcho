package com.univalle.inventorywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.inventorywidget.data.repository.FirebaseAuthRepository

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private var isBalanceVisible = false  // estado actual
        const val ACTION_TOGGLE_BALANCE = "TOGGLE_BALANCE"
        const val ACTION_MANAGE_INVENTORY = "MANAGE_INVENTORY"
        const val ACTION_SHOW_BALANCE_AFTER_LOGIN = "SHOW_BALANCE_AFTER_LOGIN"
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

        val authRepository = FirebaseAuthRepository()

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
                    // Usuario no está logueado, redirigir a HU 2.0 Login/Registro
                    // TODO: Implementar navegación a HU 2.0 (LoginActivity)
                    // Debe regresar al widget después del login para ver el saldo
                    val loginIntent = Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("RETURN_TO", "WIDGET") // Indicar que debe volver al widget
                        putExtra("SHOW_BALANCE", true) // Indicar que debe mostrar el saldo
                    }
                    context.startActivity(loginIntent)
                }
            }

            ACTION_SHOW_BALANCE_AFTER_LOGIN -> {
                // Criterio 10: Mostrar el saldo después del login exitoso
                // Esta acción se debe llamar desde LoginActivity después de hacer login exitoso
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
                // Criterio 13 y 14: Validar login antes de gestionar inventario
                if (authRepository.isUserLoggedIn()) {
                    // Criterio 14: Usuario está logueado, ir a HU 3.0 Home Inventario
                    // TODO: Implementar navegación a HU 3.0 (HomeActivity)
                    val homeIntent = Intent(context, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    context.startActivity(homeIntent)
                } else {
                    // Criterio 13: Usuario no está logueado, redirigir a HU 2.0 Login/Registro
                    // TODO: Implementar navegación a HU 2.0 (LoginActivity)
                    // Después del login debe ir a HU 3.0 Home Inventario
                    val loginIntent = Intent(context, LoginActivity::class.java).apply {
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

        // texto del saldo y del ícono según el estado
        if (isBalanceVisible) {
            views.setTextViewText(R.id.txtBalance, "$ 3.326.000,00") // ejemplo fijo
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
}
