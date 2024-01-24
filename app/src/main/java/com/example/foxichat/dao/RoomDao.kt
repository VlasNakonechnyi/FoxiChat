package com.example.foxichat.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.foxichat.dto.RoomDto

@Dao
interface RoomDao {
    @Upsert
    suspend fun insertRooms(roomDtos :Array<RoomDto>)

    @Query("SELECT * FROM roomdto")
    suspend fun getAllRooms(): Array<RoomDto>

    @Query("SELECT * FROM roomdto WHERE id = :roomId")
    suspend fun getRoom(roomId: String): RoomDto

    @Query("DELETE FROM roomdto")
    suspend fun deleteAllRooms()


}