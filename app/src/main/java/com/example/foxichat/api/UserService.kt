package com.example.foxichat.api

import com.example.foxichat.dto.UserDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("/users/create-user")
    fun createUser(@Body body: UserDto): Call<ResponseBody>
}