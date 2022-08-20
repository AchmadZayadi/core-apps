package com.sesolutions.sesdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Location::class], version = 10, exportSchema = false)
abstract class CartRoomDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: CartRoomDatabase? = null

        fun getDatabase(context: Context): CartRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create database here
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartRoomDatabase::class.java,
                    "cart_db"
                )
                    .allowMainThreadQueries() //allows Room to executing task in main thread
                    .fallbackToDestructiveMigration() //allows Room to recreate database if no migrations found
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

//    abstract fun getCartDao() : CartDao
//    abstract fun getSupplierDao() : SuppplierDao
//    abstract fun getInboxPushDao() : InboxPushDao
//    abstract fun getPhonePrefixDao() : PhonePrefixDao
//    abstract fun getKumTempDao() : KUMTempDao
    abstract fun getLocationDao() : LocationDao

}
