package com.example.foxichat.model

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foxichat.dto.Room

@Database(entities = [Room::class], version = 2)

@TypeConverters(com.example.foxichat.service.Converters::class)
abstract class RoomsDatabase: RoomDatabase() {
    abstract fun roomDao(): RoomDao

    companion object {
        @Volatile private var instance: RoomsDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "room"

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }
        private fun buildDatabase(context: Context) = androidx.room.Room.databaseBuilder(
            context.applicationContext,
            RoomsDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

}