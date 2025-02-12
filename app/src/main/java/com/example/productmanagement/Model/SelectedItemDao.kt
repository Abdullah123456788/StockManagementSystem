package com.example.productmanagement.Model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SelectedItemDao {
    @Insert
    suspend fun insert(selectedItem: SelectedItem)

    @Query("SELECT * FROM selected_item LIMIT 1")
    suspend fun getSelectedItem(): SelectedItem?
}
