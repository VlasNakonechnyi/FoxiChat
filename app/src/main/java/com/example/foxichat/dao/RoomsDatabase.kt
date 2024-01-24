package com.example.foxichat.dao

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.RoomDto
import com.example.foxichat.service.Converters
@Database(entities = [RoomDto::class, MessageDto::class], version = 6)

@TypeConverters(Converters::class)
abstract class RoomsDatabase: RoomDatabase() {
    abstract fun roomDao(): RoomDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile private var instance: RoomsDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "roomdto"

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