package com.example.foxichat.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class RoomDto (
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: String,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "users")
    @SerializedName("users")
    val users: List<String>,

    @ColumnInfo(name = "timestamp")
    @SerializedName("timestamp")
    val timeStamp: String
)