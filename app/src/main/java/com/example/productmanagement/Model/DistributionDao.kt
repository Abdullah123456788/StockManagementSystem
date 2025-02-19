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

    @Query("SELECT * FROM distribute WHERE item = :itemName AND location = :location AND userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getDistributionByItem(itemName: String,location: String,userId: String): DistributeRecordItems?
    @Update
    suspend fun updateDistribution(record: DistributeRecordItems)
    @Query("SELECT * FROM distribute WHERE item = :itemName AND userId = :userId LIMIT 1")
    suspend fun getItemByName(itemName: String, userId: String): DistributeRecordItems?
    @Query("SELECT * FROM distribute WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllDistributions(userId: String): List<DistributeRecordItems>
}

