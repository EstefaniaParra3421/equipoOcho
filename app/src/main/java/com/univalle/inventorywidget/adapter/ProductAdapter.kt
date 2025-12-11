package com.univalle.inventorywidget.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.data.model.Product
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter para mostrar la lista de productos en un RecyclerView
 * Usa DiffUtil para optimizar actualizaciones
 */
class ProductAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    // Formato de número colombiano (HU 1.0)
    private val numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view, onProductClick, numberFormat)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder que mantiene las referencias a las vistas del item
     */
    class ProductViewHolder(
        itemView: View,
        private val onProductClick: (Product) -> Unit,
        private val numberFormat: NumberFormat
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductId: TextView = itemView.findViewById(R.id.tvProductId)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)

        fun bind(product: Product) {
            tvProductName.text = product.name
            tvProductId.text = "ID: ${product.id}"
            tvProductPrice.text = "$ ${numberFormat.format(product.price)}"

            // Click en el item
            itemView.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    /**
     * DiffUtil.ItemCallback para comparar productos de manera eficiente
     */
    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}

