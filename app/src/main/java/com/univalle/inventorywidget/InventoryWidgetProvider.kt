package com.univalle.inventorywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private var isBalanceVisible = false  // estado actual
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

        if (intent.action == "TOGGLE_BALANCE") {
            isBalanceVisible = !isBalanceVisible

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = context.packageName
            val ids = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(context, InventoryWidgetProvider::class.java)
            )

            // actualiza todos los widgets visibles
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
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

        // Intent para el botón del ojo
        val intent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "TOGGLE_BALANCE"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.iconEye, pendingIntent)

        appWidgetManager.updateAppWidget(widgetId, views)
    }
}
