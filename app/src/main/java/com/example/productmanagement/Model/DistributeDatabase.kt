//package com.example.productmanagement
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.productmanagement.Dao.DistributionDao
//import com.example.productmanagement.Model.DistributeRecordItems
//
//@Database(entities = [DistributeRecordItems::class], version = 1, exportSchema = false)
//abstract class DistributeDatabase : RoomDatabase() {
//
//    abstract fun distributionDao(): DistributionDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: DistributeDatabase? = null
//
//        fun getDatabase(context: Context): DistributeDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    DistributeDatabase::class.java,
//                    "distribute_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}
