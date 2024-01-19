package com.example.foxichat.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // TODO NOTE: Base URL is usually saved as a constant in gradle using buildConfigField
    private const val BASE_URL = "http://192.168.88.44:3000/"

    // TODO NOTE: HttpLoggingInterceptor can be useful to log http events in LogCat. Try to add it
    fun getClient(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
        .build()
}