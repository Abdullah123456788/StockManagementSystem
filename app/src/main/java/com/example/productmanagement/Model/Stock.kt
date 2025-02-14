package com.example.productmanagement.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock")
data class Stock(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var item: String,
    var quantity: Int,
    var location:String,
    val timestamp: String
)
