package com.univalle.inventorywidget

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "inventory.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_PRODUCTS = "products"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_QUANTITY INTEGER NOT NULL,
                $COLUMN_PRICE REAL NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    fun insertProduct(name: String, category: String, quantity: Int, price: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_QUANTITY, quantity)
            put(COLUMN_PRICE, price)
        }
        return db.insert(TABLE_PRODUCTS, null, values)
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_CATEGORY, COLUMN_QUANTITY, COLUMN_PRICE),
            null, null, null, null,
            "$COLUMN_ID ASC"
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
            products.add(Product(id, name, category, quantity, price))
        }
        cursor.close()
        return products
    }

    fun getProductCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_PRODUCTS", null)
        val count = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            0
        }
        cursor.close()
        return count
    }

    fun deleteProduct(productId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_PRODUCTS, "$COLUMN_ID = ?", arrayOf(productId.toString()))
        return result > 0
    }

    fun getProductTotal(productId: Int): Double? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            arrayOf("$COLUMN_PRICE * $COLUMN_QUANTITY as total"),
            "$COLUMN_ID = ?",
            arrayOf(productId.toString()),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            cursor.getDouble(0)
        } else {
            null
        }.also { cursor.close() }
    }
}

