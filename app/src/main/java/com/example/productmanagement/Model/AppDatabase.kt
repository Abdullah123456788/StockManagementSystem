package com.example.productmanagement.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [loginitem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao

    companion object {
        private const val DATABASE_NAME = "app_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Creating the users table with the correct schema
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `users` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `Email` TEXT, 
                `password` TEXT
            )
        """)
            }
        }


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )

                    .addMigrations(MIGRATION_1_2) // Add migration here
                    .fallbackToDestructiveMigration() // Optional, you can keep this to delete the database if migration fails
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
