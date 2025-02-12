package com.example.productmanagement.Model

import com.example.productmanagement.Model.Converter
import com.example.productmanagement.Model.ItemsDao  // Correct import statement
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.productmanagement.Model.DistributeRecordItems
import com.example.productmanagement.Model.DistributionDao
import com.example.productmanagement.Model.Item

@Database(entities = [Item::class,DistributeRecordItems::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)

abstract class ItemsDatabase : RoomDatabase() {

    abstract fun itemsDao():ItemsDao
    abstract fun DistributionDao(): DistributionDao

    companion object {
        @Volatile
        private var INSTANCE: ItemsDatabase? = null

        fun getDatabase(context: Context): ItemsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemsDatabase::class.java,
                    "items_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
