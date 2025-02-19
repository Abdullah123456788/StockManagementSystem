package com.example.productmanagement.Model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseItem>)

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllExpenses(userId: String): List<ExpenseItem>

    @Query("DELETE FROM expenses WHERE userId = :userId")
    suspend fun deleteAllExpenses(userId: String)
}
