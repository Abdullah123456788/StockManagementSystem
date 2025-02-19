package com.example.productmanagement.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class loginitem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var Email:String,
    var password:String
)
