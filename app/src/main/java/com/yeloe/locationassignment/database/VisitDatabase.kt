package com.yeloe.locationassignment.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yeloe.locationassignment.doa.VisitDao
import com.yeloe.locationassignment.model.Visit

@Database(entities = [Visit::class], version = 1)
abstract class VisitDatabase : RoomDatabase() {
    abstract fun visitDao(): VisitDao

    companion object {
        @Volatile
        private var INSTANCE: VisitDatabase? = null

        fun getInstance(context: Context): VisitDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VisitDatabase::class.java,
                    "visit_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}