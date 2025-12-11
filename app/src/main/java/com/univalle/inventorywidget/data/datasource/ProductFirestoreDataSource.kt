package com.univalle.inventorywidget.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.data.model.Product
import kotlinx.coroutines.tasks.await

class ProductFirestoreDataSource(
    private val firestore: FirebaseFirestore
) {

    private val productsRef = firestore.collection("products")

    suspend fun insertProduct(product: Product) {
        // Genera ID automático en Firestore
        // Convertir Product a Map para evitar problemas de serialización con Room
        val productMap = hashMapOf(
            "code" to product.code,
            "name" to product.name,
            "price" to product.price,
            "quantity" to product.quantity
        )
        // Si el producto tiene un ID válido (> 0), usarlo como documento
        // Si no, Firestore generará un ID automáticamente
        if (product.id > 0) {
            productsRef.document(product.id.toString()).set(productMap).await()
        } else {
            productsRef.add(productMap).await()
        }
    }

    suspend fun getAllProducts(): List<Product> {
        val snapshot = productsRef.get().await()
        return snapshot.documents.mapIndexedNotNull { index, document ->
            try {
                // Leer los datos del documento como Map
                val data = document.data
                if (data != null) {
                    // Intentar usar el ID del documento si es numérico, sino usar el índice + 1
                    val documentId = document.id.toIntOrNull() ?: (index + 1)
                    Product(
                        id = documentId,
                        code = (data["code"] as? Number)?.toInt() ?: 0,
                        name = data["name"] as? String ?: "",
                        price = (data["price"] as? Number)?.toDouble() ?: 0.0,
                        quantity = (data["quantity"] as? Number)?.toInt() ?: 0
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
