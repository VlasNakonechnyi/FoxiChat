package com.example.foxichat.dto

import com.google.gson.annotations.SerializedName

data class UserDto (
    @SerializedName("email")
    val email: String,

    @SerializedName("display_name")
    val displayName: String,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("photo_url")
    val photoUrl: String
)