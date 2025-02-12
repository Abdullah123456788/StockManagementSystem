package com.example.productmanagement.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SelectedItem::class], version = 1, exportSchema = false)
abstract class SelectedItemDatabase : RoomDatabase() {
    abstract fun selectedItemDao(): SelectedItemDao

    companion object {
        @Volatile
        private var INSTANCE: SelectedItemDatabase? = null

        fun getDatabase(context: Context): SelectedItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SelectedItemDatabase::class.java,
                    "selected_items_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
