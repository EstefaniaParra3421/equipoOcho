package com.univalle.inventorywidget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)
