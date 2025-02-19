package com.example.productmanagement.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val item: String,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String
)

