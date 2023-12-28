package com.example.foxichat.model

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavHostController
import com.example.foxichat.api.ApiFactory
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.User
import com.example.foxichat.navigation.Screen
import com.example.foxichat.service.Converters
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteRepository {
    val TAG = "REMOTE_REPO"
    private val retrofit = RetrofitClient.getClient()
    val api = retrofit.create(ApiFactory::class.java)
    var roomsList: List<Room> = emptyList()
    fun createUser(
        nav: NavHostController,
        scope: CoroutineScope,
        hostState: SnackbarHostState,
        user: User
    ) {
        api.createUserRequest(
            user
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("RESPONSE_BODY", response.body().toString())
                nav.navigate(Screen.SIGNIN.name)
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