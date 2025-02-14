package com.example.productmanagement.Model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: Stock)

    @Query("SELECT * FROM stock")
    suspend fun getAllStock(): List<Stock>

    @Query("UPDATE stock SET quantity = quantity + :quantity, timestamp = :timestamp WHERE item = :itemName")
    suspend fun updateStockItem(itemName: String, quantity: Int, timestamp: String)
}

