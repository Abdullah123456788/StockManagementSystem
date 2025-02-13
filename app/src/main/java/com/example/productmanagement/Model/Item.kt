package com.example.productmanagement.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.productmanagement.Distribution

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val items: String,
    var quantity: Int = 0,
    var distributionQuantity: Int,
    var timestamp: Long = 0
)
