package com.example.foxichat.model

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room

@Database(entities = [Room::class, MessageDto::class], version = 4)

/* TODO NOTE: Check imports across the app, you need to include import in the top of the file
*   import com.example.foxichat.service.Converters
*   @TypeConverters(Converters::class)
* */
@TypeConverters(com.example.foxichat.service.Converters::class)
abstract class RoomsDatabase: RoomDatabase() {
    abstract fun roomDao(): RoomDao
    abstract fun messageDao(): MessageDao

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