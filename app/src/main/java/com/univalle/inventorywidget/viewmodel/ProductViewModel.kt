package com.univalle.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import com.univalle.inventorywidget.data.database.AppDatabase
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "inventory_db"
    ).build()

    private val repository = ProductRepository(db.productDao())

    val allProducts = repository.allProducts.asLiveData()

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            repository.insert(product)
        }
    }
}
