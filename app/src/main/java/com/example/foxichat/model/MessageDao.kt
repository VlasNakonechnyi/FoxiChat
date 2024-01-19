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

// TODO NOTE: Dao is not a model to put it in this package.
//  1.
//  dto and models are the packages that hold application objects (data classes)
//  dto - is a raw object (also called entity) that comes from remote (BE, etc)
//  model - is a local object that is created (mapped) from a remote dto object. Only local object
//  can be used to update UI and other internal application logic. So we receive the DTO from remote
//  source and map it to local object, only after that we can manipulate received data.
//  dto package holds dto objects - MessageDto / MessageEntity
//  model package holds model objects - Message / MessageModel
//  2.
//  Database related .
//  .
