package com.example.foxichat.model

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import com.example.foxichat.api.ApiFactory
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.dto.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteRepository {
    private val retrofit = RetrofitClient.getClient()
    private val api = retrofit.create(ApiFactory::class.java)
    fun createUser(scope: CoroutineScope, hostState: SnackbarHostState, user: User) {
        api.createUserRequest(
            user
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("RESPONSE_BODY", response.body().toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                scope.launch {
                   hostState.showSnackbar(
                        message = "Something went wrong",
                    )
                }
            }
        })
       // Log.d("RESPONSE_BODY", response.toString())
    }
}