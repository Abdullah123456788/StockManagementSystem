package com.example.productmanagement.Model

import androidx.room.*


@Dao
interface ItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Update
    suspend fun updateItem(item: Item)

    @Query("SELECT * FROM items WHERE items = :itemName LIMIT 1 ")
    suspend fun getItemByName(itemName: String): Item?

    @Query("SELECT * FROM items ORDER BY timestamp DESC")
    suspend fun getAllItems(): List<Item>

}


