package com.example.productmanagement.Model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.productmanagement.Distribution

@Dao
interface DistributionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistribution(distribution: DistributeRecordItems)

    @Query("SELECT * FROM distribute WHERE item = :itemName AND location = :location ORDER BY timestamp DESC LIMIT 1")
    suspend fun getDistributionByItem(itemName: String,location: String): DistributeRecordItems?

    @Update
    fun updateDistribution(record: DistributeRecordItems)
    @Query("SELECT * FROM distribute WHERE item = :itemName LIMIT 1")
    fun getItemByName(itemName: String): DistributeRecordItems?
    @Query("SELECT * FROM distribute")
    fun getAllDistributions(): List<DistributeRecordItems>

}

