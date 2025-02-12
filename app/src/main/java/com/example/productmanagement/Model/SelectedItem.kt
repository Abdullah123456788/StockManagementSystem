package com.example.productmanagement.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_item")
data class SelectedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String
)
