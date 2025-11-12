package com.univalle.inventorywidget.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy
import com.univalle.inventorywidget.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>
}
