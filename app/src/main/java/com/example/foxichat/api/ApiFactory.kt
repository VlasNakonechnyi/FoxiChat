package com.example.foxichat.api

import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.UserDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiFactory {
    @POST("/tokens")
    fun postRequest(@Body body: Map<String, String>): Call<ResponseBody>

    @POST("/messages/send-message")
    fun sendMessage(@Body body: MessageDto) : Call<ResponseBody>

    @GET("/messages/get-messages")
    fun getMessagesFromRoom(@Query("roomid") string: String): Call<ResponseBody>

    @POST("/users/create-user")
    fun createUserRequest(@Body body: UserDto): Call<ResponseBody>

    @GET("/rooms/get-all-rooms")
    fun getAllRooms(): Call<ResponseBody>
    @POST("/rooms/get-user-rooms")
    fun getUserRooms(@Body body: String): Call<ResponseBody>

    @POST("/rooms/create-room")
    fun createRoom(@Body body: Room): Call<ResponseBody>

    @POST("/rooms/join-room")
    fun joinRoom(@Body body: Map<String, String>): Call<ResponseBody>
}