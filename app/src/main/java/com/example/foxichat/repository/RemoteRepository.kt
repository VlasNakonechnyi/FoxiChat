package com.example.foxichat.repository

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.example.foxichat.api.ApiFactory
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.auth
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.UserDto
import com.example.foxichat.model.RoomsDatabase
import com.example.foxichat.navigation.Screen
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime

// TODO NOTE: repository classes should not be stored in the model package
class RemoteRepository(
    private val api: ApiFactory,
    private val app: Application
) {



    val TAG = "REMOTE_REPO"
    /* TODO NOTE: Check what is Dependency injection (DI). Popular libraries are Dagger, Hilt and Koin.
        Google recommends to use Hilt (which is build on top of Dagger)
        Using DI you'll get rid of manually created retrofit, repositories, factories, services and viewmodels.
        Also passing the context to the repository or saving it in the viewmodel would be unnecessary */
    private val retrofit = RetrofitClient.getClient()


    // TODO NOTE: DO not store data in the repository as it can be returned directly in the
    //  viewmodel as a function result
    val userRoomList: MutableLiveData<List<Room>> by lazy {
        MutableLiveData<List<Room>>()
    }
    val roomsList: MutableLiveData<List<Room>> by lazy {
        MutableLiveData<List<Room>>()
    }


    companion object {
        val messages: MutableLiveData<MutableList<MessageDto>> by lazy {
            MutableLiveData<MutableList<MessageDto>>()
        }
        var currentChatId = ""
        fun addToCurrentMessages(msg: MessageDto) {

            if (currentChatId == msg.roomId) {
                add(msg)
            }
        }
        private fun add(msg: MessageDto) {

            val list = messages.value?.let { ArrayList(it) }
            list?.add(msg)
            CoroutineScope(Dispatchers.Main).launch {
                messages.value = list
            }

        }
    }


    fun createUser(
        nav: NavHostController,
        scope: CoroutineScope,
        hostState: SnackbarHostState,
        userDto: UserDto
    ) {
        api.createUser(
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

        name: String,
        creatorId: String,
    ) {
        val room = Room(
            id = "0".repeat(24),
            name = name,
            users = listOf(creatorId),
            timeStamp = timeToDbFormat()
        )
        api.createRoom(room).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    CoroutineScope(Dispatchers.Main).launch {
                        hostState.showSnackbar(
                            message = "Room created successfully",
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                CoroutineScope(Dispatchers.Main).launch {
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
        uid: String
    ) {
        api.joinRoom(
            mapOf(
                "room_id" to roomId,
                "uid" to uid
            )
        ).enqueue(object : Callback<ResponseBody> {
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
        roomId: String,
        onReadyChange: (Boolean) -> Unit
    ) {
        currentChatId = roomId
        Log.d(TAG, roomId)
        val gson = Gson()
        api.getMessagesFromRoom(roomId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                response.body()?.string()?.let {

                    CoroutineScope(Dispatchers.IO).launch {
                        val messages =
                            gson.fromJson(it, Array<MessageDto>::class.java)
                                .asList()
                        insertMessagesToLocalDb(messages)
                        val res = async { getMessagesFromLocalDb(roomId) }
                        val list = res.await()
                        Log.d(TAG, "LIST FROM DB $list")
                        withContext(Dispatchers.Main) {
                            Companion.messages.value = list
                            onReadyChange(true)
                        }

                    }

                    Log.d(TAG, messages.toString())
                }


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                CoroutineScope(Dispatchers.IO).launch {
                    val res = async { getMessagesFromLocalDb(roomId) }
                    withContext(Dispatchers.Main) {
                        messages.value = res.await()
                        onReadyChange(true)
                    }

                }
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
            api.sendMessage(messageDto).enqueue(object : Callback<ResponseBody> {
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
                    apiService.sendNotificationToken(body).enqueue(object : Callback<ResponseBody> {
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

    suspend fun insertMessagesToLocalDb(messages: List<MessageDto>) {
        Log.d(TAG, "Messages into db")
        RoomsDatabase(app.applicationContext).messageDao().insertMessages(
            messages
        )

    }

    suspend fun getMessagesFromLocalDb(id: String): MutableList<MessageDto> {
        Log.d(TAG, "Messages from db ${RoomsDatabase(app.applicationContext).messageDao().getMessagesFromLocalDb(id)}")
        return RoomsDatabase(app.applicationContext).messageDao().getMessagesFromLocalDb(id)
    }

    fun loadUserRooms(
        onReadyChange: (Boolean) -> Unit
    ) {
        val gson = Gson()
        api.getUserRooms(auth.uid.toString())
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //CoroutineScope(Dispatchers.IO).launch{ deleteAllRooms()}
                    response.body()?.string()?.let {
                        val rooms = gson.fromJson(it, Array<Room>::class.java).asList()
                        CoroutineScope(Dispatchers.IO).launch {
                            insertRoomsToLocalDb(rooms)
                            val res = async { getRoomsFromLocalDb() }
                            val list = res.await()
                            withContext(Dispatchers.Main) {
                                userRoomList.value = list
                                onReadyChange(true)
                            }
                            Log.d("LOAD_USER_ROOMS", rooms.toString())

                        }

                    }
                }

                override fun onFailure(vcall: Call<ResponseBody>, t: Throwable) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val res = async { getRoomsFromLocalDb() }
                        withContext(Dispatchers.Main) {
                            userRoomList.value = res.await()
                            onReadyChange(true)
                        }

                    }
                }
            })


    }
    fun loadAllRooms(onReadyChange: (Boolean) -> Unit) {
        val gson = Gson()
        api.getAllRooms().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                response.body()?.string()?.let {
                    val rooms = gson.fromJson(it, Array<Room>::class.java).asList()
                    println("ROOMS: $rooms")
                    roomsList.value = rooms
                    onReadyChange(true)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onReadyChange(true)
            }
        })
    }
    private suspend fun insertRoomsToLocalDb(rooms: List<Room>) {
        //RoomsDatabase(getApplication()).roomDao().deleteAllRooms()
        RoomsDatabase(app.applicationContext).roomDao().insertRooms(rooms)
    }
    private suspend fun deleteAllRooms() {
        RoomsDatabase(app.applicationContext).roomDao().deleteAllRooms()
    }

    private suspend fun getRoomsFromLocalDb(): SnapshotStateList<Room> {
        return RoomsDatabase(app.applicationContext).roomDao().getAllRooms().toMutableStateList()
    }


}