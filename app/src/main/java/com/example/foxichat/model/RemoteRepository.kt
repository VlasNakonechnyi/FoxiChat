package com.example.foxichat.model

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.example.foxichat.api.ApiFactory
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.UserDto
import com.example.foxichat.navigation.Screen
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime

class RemoteRepository {
    val TAG = "REMOTE_REPO"
    private val retrofit = RetrofitClient.getClient()
    val api: ApiFactory = retrofit.create(ApiFactory::class.java)
    var chatState = LazyListState(0)
    companion object {
        val messages: MutableLiveData<MutableList<MessageDto>> by lazy {
            MutableLiveData<MutableList<MessageDto>>()
        }
    }

    fun createUser(
        nav: NavHostController,
        scope: CoroutineScope,
        hostState: SnackbarHostState,
        userDto: UserDto
    ) {
        api.createUserRequest(
            userDto
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
    fun createNewRoom(

        hostState: SnackbarHostState,
        scope: CoroutineScope,
        name: String,
        creatorId: String,
    ) {
        val room = Room(
            id = "000000000000000000000000",
            name = name,
            users = listOf(creatorId),
            timeStamp = timeToDbFormat()
        )
        api.createRoom(room).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    scope.launch {
                        hostState.showSnackbar(
                            message = "Room created successfully",
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                scope.launch {
                    hostState.showSnackbar(
                        message = "Something went wrong",
                    )
                }
            }

        })
    }
    fun timeToDbFormat(): String {

        return "${LocalDate.now()}T${LocalTime.now().toString().substringBefore(".") + "Z"}"
    }

    fun addUserToRoom(
                      hostState: SnackbarHostState,
                      roomId: String,
                      uid: String) {
        api.joinRoom(mapOf(
            "room_id" to roomId,
            "uid" to uid
        )).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                CoroutineScope(Dispatchers.Main).launch {
                    hostState.showSnackbar(
                        message = "Joined"
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                CoroutineScope(Dispatchers.Main).launch {
                    hostState.showSnackbar(
                        message = "Something went wrong"
                    )
                }
            }

        })
    }
    fun getMessagesFromRoom(
        hostState: SnackbarHostState,
        roomId: String
    ) {
        Log.d(TAG, roomId)
        val gson = Gson()
        api.getMessagesFromRoom(roomId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                response.body()?.string()?.let {
                    messages.value =
                        gson.fromJson(it, Array<MessageDto>::class.java)
                            .asList().toMutableList()
                    chatState = LazyListState(messages.value!!.size - 1)
                    Log.d(TAG, messages.toString())
                }


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                CoroutineScope(Dispatchers.Main).launch {
                    hostState.showSnackbar(
                        message = "Something went wrong"
                    )
                }
            }

        })
    }

    fun sendMessage(messageDto: MessageDto?) {

        if (messageDto != null) {
            //addToCurrentMessages(messageDto)
            api.sendMessage(messageDto).enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d(TAG, "MESSAGE_SENT")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d(TAG, "MESSAGE_NOT_SENT")
                }

            })
        }

    }
    fun addToCurrentMessages(msg: MessageDto) {
        Log.d(TAG, msg.toString())


        val list = messages.value?.let { ArrayList(it) }
        list?.add(msg)
        CoroutineScope(Dispatchers.Main).launch {
            messages.value = list
        }

    }

    fun sendNotificationToken(uid: String) {
        Firebase.messaging.token.addOnCanceledListener {

        }
        Firebase.messaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d(TAG, "Refreshed token: $token")
                val retrofit = RetrofitClient.getClient()
                val apiService = retrofit.create(ApiFactory::class.java)

                val body = mapOf(
                    "userId" to uid,
                    "deviceId" to token,
                    "timestamp" to timeToDbFormat()
                    )
                if (token != null) {
                    apiService.postRequest(body).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            Log.d(TAG, response.body().toString())
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d(TAG, t.message.toString())
                        }
                    })
                }
                // Log and toast

            },
        )
    }


}