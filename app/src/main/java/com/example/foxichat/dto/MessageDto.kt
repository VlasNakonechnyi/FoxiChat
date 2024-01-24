package com.example.foxichat.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomDto::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("room_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MessageDto(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "author_id")
    @SerializedName("author_id")
    val authorId: String,

    @ColumnInfo(name = "author_name")
    @SerializedName("author_name")
    val authorName: String,


    @ColumnInfo(name = "room_id")
    @SerializedName("room_id")
    val roomId: String,

    @ColumnInfo(name = "body")
    @SerializedName("body")
    val body: String,

    @ColumnInfo(name = "timestamp")
    @SerializedName("timestamp")
    val timeStamp: String,
)