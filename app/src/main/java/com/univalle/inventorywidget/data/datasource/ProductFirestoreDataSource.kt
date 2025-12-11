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
        // Usar toList() para asegurar un orden consistente
        val documents = snapshot.documents.toList()
        return documents.mapIndexedNotNull { index, document ->
            try {
                // Leer los datos del documento como Map
                val data = document.data
                if (data != null) {
                    // Usar el índice + 1 como ID para mantener consistencia
                    val documentId = index + 1
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
    
    /**
     * Elimina un producto de Firestore
     * El productId es el índice + 1 que se asignó al leer los productos
     * Necesitamos encontrar el documento correcto usando ese índice
     * IMPORTANTE: El orden de los documentos debe ser consistente
     */
    suspend fun deleteProduct(productId: Int) {
        // Primero obtener todos los productos para encontrar el producto con ese ID
        val products = getAllProducts()
        val productToDelete = products.find { it.id == productId }
        
        if (productToDelete == null) {
            throw Exception("No se pudo encontrar el producto con ID: $productId")
        }
        
        // Ahora buscar el documento en Firestore usando el código del producto
        // (asumiendo que el código es único)
        val snapshot = productsRef.get().await()
        val documents = snapshot.documents.toList()
        
        // Buscar el documento que tenga el mismo código
        val documentToDelete = documents.find { doc ->
            val data = doc.data
            val code = (data?.get("code") as? Number)?.toInt()
            code == productToDelete.code
        }
        
        if (documentToDelete != null) {
            documentToDelete.reference.delete().await()
        } else {
            // Fallback: usar el índice como antes
            val documentIndex = productId - 1
            if (documentIndex >= 0 && documentIndex < documents.size) {
                documents[documentIndex].reference.delete().await()
            } else {
                throw Exception("No se pudo encontrar el documento del producto con ID: $productId")
            }
        }
    }
}
