package com.univalle.inventorywidget.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.univalle.inventorywidget.data.dao.ProductDao
import com.univalle.inventorywidget.data.model.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
