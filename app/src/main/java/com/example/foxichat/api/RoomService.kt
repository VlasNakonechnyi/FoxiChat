package com.example.foxichat.api

import com.example.foxichat.dto.RoomDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RoomService {
    @GET("/rooms/get-all-rooms")
    suspend fun getAllRooms(): ResponseBody
    @POST("/rooms/get-user-rooms")
    suspend fun getUserRooms(@Body body: String): ResponseBody

    @POST("/rooms/create-room")
    fun createRoom(@Body body: RoomDto): Call<ResponseBody>

    @POST("/rooms/join-room")
    fun joinRoom(@Body body: Map<String, String>): Call<ResponseBody>
}