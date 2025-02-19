package com.example.productmanagement.Model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LoginDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: loginitem)

    @Query("SELECT * FROM users")
    suspend fun getUser(): List<loginitem>
}
