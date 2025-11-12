package com.univalle.inventorywidget.data.repository

import androidx.lifecycle.LiveData
import com.univalle.inventorywidget.data.dao.ProductDao
import com.univalle.inventorywidget.data.model.Product

class ProductRepository(private val productDao: ProductDao) {

    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    suspend fun insert(product: Product) {
        productDao.insertProduct(product)
    }
}
