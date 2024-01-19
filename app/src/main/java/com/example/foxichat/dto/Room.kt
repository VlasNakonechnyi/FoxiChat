package com.example.foxichat.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// TODO NOTE: If you decided to use "Dto" in entity names, make sure to apply it uniformly across
//  all relevant entities. Consistency in naming conventions is crucial
@Entity
data class Room (
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