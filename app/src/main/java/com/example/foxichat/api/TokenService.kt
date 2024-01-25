package com.example.foxichat.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {
    @POST("/tokens")
    fun sendNotificationToken(@Body body: Map<String, String>): Call<ResponseBody>

}