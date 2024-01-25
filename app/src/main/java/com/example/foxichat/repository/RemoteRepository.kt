package com.example.foxichat.repository

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.navigation.NavHostController
import com.example.foxichat.AuthenticationWorker
import com.example.foxichat.api.MessagingService
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.api.RoomService
import com.example.foxichat.api.TokenService
import com.example.foxichat.api.UserService
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.RoomDto
import com.example.foxichat.dto.UserDto
import com.example.foxichat.dao.RoomsDatabase
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

class RemoteRepository(
    private val userService: UserService,
    private val tokenService: TokenService,
    private val roomService: RoomService,
    private val messagingService: MessagingService,
    private val app: Application
) {
    val TAG = "REMOTE_REPO"

    companion object {

        var currentChatId = ""
        fun addToCurrentMessages(msg: MessageDto) {

//            if (currentChatId == msg.roomId) {
//                add(msg)
//            }
        }

//        private fun add(msg: MessageDto) {
//
//            val list = messages.value?.let { ArrayList(it) }
//            list?.add(msg)
//            CoroutineScope(Dispatchers.Main).launch {
//                messages.value = list
//            }
//
//        }
    }


    fun createUser(
        nav: NavHostController,
        scope: CoroutineScope,
        hostState: SnackbarHostState,
        userDto: UserDto
    ) {
        userService.createUser(
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

    }

    fun createNewRoom(

        hostState: SnackbarHostState,

        name: String,
        creatorId: String,
    ) {
        val roomDto = RoomDto(
            id = "0".repeat(24),
            name = name,
            users = listOf(creatorId),
            timeStamp = timeToDbFormat()
        )
        roomService.createRoom(roomDto).enqueue(object : Callback<ResponseBody> {
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
        roomService.joinRoom(
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


    // Get messages for the specific room from the backend
    suspend fun getMessagesFromRoom(
        hostState: SnackbarHostState,
        roomId: String,
        onReadyChange: (Boolean) -> Unit
    ): MutableList<MessageDto> {
        currentChatId = roomId
        Log.d(TAG, roomId)
        val gson = Gson()
        try {
            val response = messagingService.getMessagesFromRoom(roomId)
            response.string().let {
                val messages =
                    gson.fromJson(it, Array<MessageDto>::class.java)

                insertMessagesToLocalDb(messages)
                onReadyChange(true)
                return getMessagesFromLocalDb(roomId)
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                hostState.showSnackbar(
                    message = "Something went wrong"
                )
            }
            onReadyChange(true)

            return getMessagesFromLocalDb(roomId)
        }
    }


    fun sendMessage(messageDto: MessageDto?) {

        if (messageDto != null) {
            //addToCurrentMessages(messageDto)
            messagingService.sendMessage(messageDto).enqueue(object : Callback<ResponseBody> {
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


                val body = mapOf(
                    "userId" to uid,
                    "deviceId" to token,
                    "timestamp" to timeToDbFormat()
                )
                if (token != null) {
                    tokenService.sendNotificationToken(body).enqueue(object : Callback<ResponseBody> {
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

    private suspend fun insertMessagesToLocalDb(messages: Array<MessageDto>) {
        Log.d(TAG, "Messages into db")
        RoomsDatabase(app.applicationContext).messageDao().insertMessages(
            messages
        )
    }

    private suspend fun getMessagesFromLocalDb(id: String): MutableList<MessageDto> {
        return RoomsDatabase(app.applicationContext).messageDao().getMessagesFromLocalDb(id).toMutableList()
    }
    suspend fun loadUserRooms(
        onReadyChange: (Boolean) -> Unit
    ): SnapshotStateList<RoomDto> {
        try {
            val gson = Gson()
            val response = roomService.getUserRooms(AuthenticationWorker.auth.uid.toString())
            response.string().let {
                val roomDtos = gson.fromJson(it, Array<RoomDto>::class.java)
                insertRoomsToLocalDb(roomDtos)
                onReadyChange(true)
                return getRoomsFromLocalDb()
            }
        } catch (e: Exception) {
            onReadyChange(true)
            return getRoomsFromLocalDb()
        }
    }

    suspend fun loadAllRooms(onReadyChange: (Boolean) -> Unit): List<RoomDto> {
        try {
            val gson = Gson()
            val response = roomService.getAllRooms()
            response.string().let {
                val roomDtos = gson.fromJson(it, Array<RoomDto>::class.java).asList()
                println("ROOMS: $roomDtos")
                onReadyChange(true)
                return roomDtos
            }
        } catch (e: Exception) {
            onReadyChange(true)
            return emptyList()
        }
    }

    private suspend fun insertRoomsToLocalDb(roomDtos: Array<RoomDto>) {
        //RoomsDatabase(getApplication()).roomDao().deleteAllRooms()
        RoomsDatabase(app.applicationContext).roomDao().insertRooms(roomDtos)
    }

    private suspend fun deleteAllRooms() {
        RoomsDatabase(app.applicationContext).roomDao().deleteAllRooms()
    }

    private suspend fun getRoomsFromLocalDb(): SnapshotStateList<RoomDto> {
        return RoomsDatabase(app.applicationContext).roomDao().getAllRooms().toList().toMutableStateList()
    }

    fun signIn(nav: NavHostController, email: String, password: String) {
        AuthenticationWorker.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = AuthenticationWorker.auth.currentUser
                    Log.d("USER_SIGNED_IN", user?.uid.toString())
                    nav.navigate(Screen.HOME.name)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.d("USER_SIGNED_IN", user?.uid.toString())
                }
            }

    }

}