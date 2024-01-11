package com.example.foxichat.model

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.foxichat.dto.MessageDto

@Dao
interface MessageDao {
    @Upsert
    suspend fun insertMessages(rooms :List<MessageDto>)

    @Query("SELECT * FROM messagedto WHERE room_id = :roomId")
    suspend fun getMessagesFromLocalDb(roomId: String): MutableList<MessageDto>


}