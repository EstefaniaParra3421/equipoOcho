package com.univalle.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import com.univalle.inventorywidget.data.database.AppDatabase
import com.univalle.inventorywidget.data.model.Product
import com.univalle.inventorywidget.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    val products = repository.products

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            repository.insert(product)
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            repository.refreshProducts()
        }
    }
    
    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            repository.delete(productId)
        }
    }
}
