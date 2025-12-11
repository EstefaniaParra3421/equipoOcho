package com.univalle.inventorywidget.data.repository

import com.univalle.inventorywidget.data.datasource.ProductFirestoreDataSource
import com.univalle.inventorywidget.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductRepository(
    private val firestoreDataSource: ProductFirestoreDataSource
) {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    suspend fun insert(product: Product) {
        firestoreDataSource.insertProduct(product)
        refreshProducts()
    }

    suspend fun refreshProducts() {
        _products.value = firestoreDataSource.getAllProducts()
    }
}
