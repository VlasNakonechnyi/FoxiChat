package com.example.foxichat.api

import com.example.foxichat.dto.Room
import com.example.foxichat.dto.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiFactory {
    @POST("/tokens")
    fun postRequest(@Body body: Map<String, String>): Call<ResponseBody>

    @POST("/users/create-user")
    fun createUserRequest(@Body body: User): Call<ResponseBody>

    @GET("/rooms/get-all-rooms")
    fun getAllRooms(): Call<ResponseBody>
    @POST("/rooms/get-user-rooms")
    fun getUserRooms(@Body body: String): Call<ResponseBody>

    @POST("/rooms/create-room")
    fun createRoom(@Body body: Room): Call<ResponseBody>
}