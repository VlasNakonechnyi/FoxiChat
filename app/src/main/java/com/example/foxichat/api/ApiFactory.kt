package com.example.foxichat.api

import com.example.foxichat.dto.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiFactory {
    @POST("/tokens")
    fun postRequest(@Body body: Map<String, String>): Call<ResponseBody>

    @POST("/users/create-user")
    fun createUserRequest(@Body body: User): Call<ResponseBody>
}