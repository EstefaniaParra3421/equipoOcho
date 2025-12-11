package com.univalle.inventorywidget.utils

import android.content.Context
import android.content.Intent
import com.univalle.inventorywidget.FragmentDemoActivity

/**
 * Helper para facilitar la navegación a diferentes actividades del proyecto
 */
object NavigationHelper {
    
    /**
     * Abre la actividad de demostración de Fragments y Navigation Component
     * 
     * Ejemplo de uso:
     * ```
     * NavigationHelper.openFragmentDemo(context)
     * ```
     */
    fun openFragmentDemo(context: Context) {
        val intent = Intent(context, FragmentDemoActivity::class.java)
        context.startActivity(intent)
    }
}

