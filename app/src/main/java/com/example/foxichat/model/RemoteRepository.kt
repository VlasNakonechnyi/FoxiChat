package com.example.foxichat.model

import android.util.Log
import com.example.foxichat.api.ApiFactory
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.entity.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteRepository {
    private val retrofit = RetrofitClient.getClient()
    private val api = retrofit.create(ApiFactory::class.java)
    fun createUser(u: User) {
        val response = api.createUserRequest(
            u
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("RESPONSE_BODY", response.body().toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //Log.d(TAG, t.message.toString())
            }
        })
       // Log.d("RESPONSE_BODY", response.toString())
    }
}