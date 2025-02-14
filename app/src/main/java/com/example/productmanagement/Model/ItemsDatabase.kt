package com.example.productmanagement.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Item::class,DistributeRecordItems::class,Stock::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)

abstract class ItemsDatabase : RoomDatabase() {

    abstract fun itemsDao():ItemsDao
    abstract fun DistributionDao(): DistributionDao
    abstract fun stockDao(): StockDao

    companion object {
        @Volatile
        private var INSTANCE: ItemsDatabase? = null

        fun getDatabase(context: Context): ItemsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemsDatabase::class.java,
                    "items_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
