package com.example.foxichat.api

import com.example.foxichat.dto.MessageDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessagingService {
    @POST("/messages/send-message")
    fun sendMessage(@Body body: MessageDto) : Call<ResponseBody>

    @GET("/messages/get-messages")
    suspend fun getMessagesFromRoom(@Query("roomid") string: String): ResponseBody
}