package com.example.productmanagement.Model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.productmanagement.Distribution

@Dao
interface DistributionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistribution(distribution: DistributeRecordItems)

    @Query("SELECT * FROM distribute WHERE item = :itemName LIMIT 1")
    suspend fun getDistributionByItem(itemName: String): DistributeRecordItems?

    @Query("UPDATE distribute SET distributionquantity = :quantity, timestamp = :timestamp, location = :location WHERE item = :itemName")
    suspend fun updateDistributionQuantity(itemName: String, quantity: Int, timestamp: String, location: String)

    @Query("SELECT * FROM distribute")
    fun getAllDistributions(): List<DistributeRecordItems>

}

