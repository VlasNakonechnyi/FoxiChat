package com.example.foxichat.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foxichat.dto.Room

@Dao
interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms :List<Room>): List<Long>

    @Query("SELECT * FROM room")
    suspend fun getAllRooms(): List<Room>

    @Query("SELECT * FROM room WHERE id = :roomId")
    suspend fun getRoom(roomId: String): Room

    @Query("DELETE FROM room")
    suspend fun deleteAllRooms()


}