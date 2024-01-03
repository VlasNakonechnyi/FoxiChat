package com.example.foxichat.dto

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("author_id")
    val authorId: String,
    @SerializedName("author_name")
    val authorName: String,
    @SerializedName("room_id")
    val roomId: String,

    @SerializedName("body")
    val body: String,
    @SerializedName("timestamp")
    val timeStamp: String,
)