package com.example.productmanagement.Model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "distribute")
data class DistributeRecordItems(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var item: String,
    var distributionquantity: Int,
    var location:String,
    val timestamp: String,
    val userId: String
)
