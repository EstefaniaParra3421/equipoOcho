package com.univalle.inventorywidget.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.data.model.Product
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para sincronizar productos con Firebase Firestore
 * Complementa el ProductRepository local (Room) con sincronización en la nube
 */
class FirestoreProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = "products"

    /**
     * Guarda un producto en Firestore
     */
    suspend fun saveProduct(product: Product): Result<Unit> {
        return try {
            db.collection(productsCollection)
                .document(product.id.toString())
                .set(
                    hashMapOf(
                        "id" to product.id,
                        "code" to product.code,
                        "name" to product.name,
                        "price" to product.price,
                        "quantity" to product.quantity
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los productos de Firestore
     */
    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snapshot = db.collection(productsCollection).get().await()
            val products = snapshot.documents.mapNotNull { document ->
                try {
                    Product(
                        id = document.getLong("id")?.toInt() ?: 0,
                        code = document.getLong("code")?.toInt() ?: 0,
                        name = document.getString("name") ?: "",
                        price = document.getDouble("price") ?: 0.0,
                        quantity = document.getLong("quantity")?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un producto específico de Firestore
     */
    suspend fun getProduct(productId: Int): Result<Product?> {
        return try {
            val document = db.collection(productsCollection)
                .document(productId.toString())
                .get()
                .await()

            if (document.exists()) {
                val product = Product(
                    id = document.getLong("id")?.toInt() ?: 0,
                    code = document.getLong("code")?.toInt() ?: 0,
                    name = document.getString("name") ?: "",
                    price = document.getDouble("price") ?: 0.0,
                    quantity = document.getLong("quantity")?.toInt() ?: 0
                )
                Result.success(product)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza un producto en Firestore
     */
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            db.collection(productsCollection)
                .document(product.id.toString())
                .update(
                    mapOf(
                        "code" to product.code,
                        "name" to product.name,
                        "price" to product.price,
                        "quantity" to product.quantity
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un producto de Firestore
     */
    suspend fun deleteProduct(productId: Int): Result<Unit> {
        return try {
            db.collection(productsCollection)
                .document(productId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza productos locales con Firestore
     * Sube todos los productos locales a la nube
     */
    suspend fun syncProductsToCloud(products: List<Product>): Result<Int> {
        return try {
            var syncedCount = 0
            products.forEach { product ->
                saveProduct(product).onSuccess {
                    syncedCount++
                }
            }
            Result.success(syncedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Escucha cambios en tiempo real de los productos
     * Útil para sincronización automática entre dispositivos
     */
    fun listenToProducts(onProductsChanged: (List<Product>) -> Unit) {
        db.collection(productsCollection)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { document ->
                        try {
                            Product(
                                id = document.getLong("id")?.toInt() ?: 0,
                                code = document.getLong("code")?.toInt() ?: 0,
                                name = document.getString("name") ?: "",
                                price = document.getDouble("price") ?: 0.0,
                                quantity = document.getLong("quantity")?.toInt() ?: 0
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    onProductsChanged(products)
                }
            }
    }
}

