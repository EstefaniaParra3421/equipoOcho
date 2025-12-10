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
        productsRef.add(product).await()
    }

    suspend fun getAllProducts(): List<Product> {
        val snapshot = productsRef.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
    }
}
