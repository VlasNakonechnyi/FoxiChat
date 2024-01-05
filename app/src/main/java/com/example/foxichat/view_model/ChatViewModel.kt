package com.example.foxichat.view_model

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.UserDto
import com.example.foxichat.model.RemoteRepository
import com.example.foxichat.model.RoomsDatabase
import com.example.foxichat.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
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

class ChatViewModel(val auth: FirebaseAuth, application: Application) :
    AndroidViewModel(application) {


    private val remoteRepository = RemoteRepository()

    val roomsList: MutableLiveData<List<Room>> by lazy {
        MutableLiveData<List<Room>>()
    }
    val userRoomList: MutableLiveData<List<Room>> by lazy {
        MutableLiveData<List<Room>>()
    }


    // *********************** INPUT VALIDATION *******************************

    fun isMessageFromMe(msg: MessageDto): Boolean {
        return msg.authorId == auth.uid.toString()
    }
    fun validatePasswordField(p: String): Boolean {
        return !p.contains(' ') && p.length >= 6
    }

    fun validatePhoneNumberField(s: String): Boolean {
        if (s.isBlank() || s.contains(' ')) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(s).matches();
        }
    }

    fun validateEmailField(s: String): Boolean {
        if (s.isBlank() || s.contains(' ')) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
        }
    }

    fun authUserNotNullDestination(): String {
        if (auth.currentUser != null) {
            sendNotificationToken()
            loadUserRooms()
            return Screen.HOME.name
        } else {
            return Screen.SIGNIN.name
        }
    }

    fun addNewUser(
        nav: NavHostController,
        email: String,
        password: String,
        username: String,
        phone: String,
        hostState: SnackbarHostState,
        scope: CoroutineScope
    ) {

        val userDto = UserDto(
            email,
            username,
            phone,
            password,
            "nothing"
        )
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.createUser(nav, scope, hostState, userDto)
        }

    }

    fun createNewRoom(
        nav: NavHostController,
        hostState: SnackbarHostState,
        scope: CoroutineScope,
        name: String
    ) {
        println("CREATE NEW ROOM WORKED")
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.createNewRoom(
                nav,
                hostState,
                scope,
                name,
                creatorId = auth.uid.toString()
            )
        }

    }

    fun signIn(nav: NavHostController, email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("USER_SIGNED_IN", user?.uid.toString())
                    nav.navigate(Screen.HOME.name)
                    sendNotificationToken()
                    loadUserRooms()
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.d("USER_SIGNED_IN", user?.uid.toString())
                }
            }

    }

    fun getAllRooms() {
        CoroutineScope(Dispatchers.IO).launch {
            loadAllRooms()
        }
    }

    private fun loadAllRooms() {
        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            remoteRepository.api.getAllRooms().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    response.body()?.string()?.let {
                        val rooms = gson.fromJson(it, Array<Room>::class.java).asList()
                        Log.d("", rooms.toString())
                        roomsList.value = rooms
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // TODO
                }
            })

        }

    }

    fun loadUserRooms() {
        Log.d("USERS_LOADING_API", "started")
        val gson = Gson()
        remoteRepository.api.getUserRooms(auth.uid.toString())
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    CoroutineScope(Dispatchers.IO).launch{ deleteAllRooms()}
                    response.body()?.string()?.let {
                        val rooms = gson.fromJson(it, Array<Room>::class.java).asList()
                        Log.d("USERS_LOADING_API", rooms.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            insertRoomsToLocalDb(rooms)
                            val res = async { getRoomsFromLocalDb() }
                            withContext(Dispatchers.Main) {
                                userRoomList.value = res.await()
                            }

                            Log.d("USERS_LOADING_FINAL", userRoomList.value.toString())
                        }

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val res = async { getRoomsFromLocalDb() }
                        withContext(Dispatchers.Main) {
                            userRoomList.value = res.await()
                        }

                        Log.d("USERS_LOADING_FINAL", userRoomList.value.toString())
                    }
                }
            })


    }

    fun signOut() {
        auth.signOut()

        CoroutineScope(Dispatchers.IO).launch{ deleteAllRooms()}



    }

    private suspend fun insertRoomsToLocalDb(rooms: List<Room>) {
        //RoomsDatabase(getApplication()).roomDao().deleteAllRooms()
        Log.d("USERS_LOADING_INTO_DB", rooms.toString())
        RoomsDatabase(getApplication()).roomDao().insertRooms(rooms)
    }
    private suspend fun deleteAllRooms() {
        Log.d("USERS_DELETED", "_______________________")
        RoomsDatabase(getApplication()).roomDao().deleteAllRooms()
    }

    private suspend fun getRoomsFromLocalDb(): SnapshotStateList<Room> {
        //RoomsDatabase(getApplication()).roomDao().deleteAllRooms()
        Log.d(
            "USERS_LOADING_FROM_DB",
            RoomsDatabase(getApplication()).roomDao().getAllRooms().toString()
        )
        return RoomsDatabase(getApplication()).roomDao().getAllRooms().toMutableStateList()
    }

    fun joinRoom(hostState: SnackbarHostState,id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.addUserToRoom(
                hostState= hostState,
                roomId = id,
                uid = auth.uid.toString())
        }
    }

    fun sendMessage(body: String, chatId: String) {
        println(auth.currentUser?.displayName)
        val messageDto = auth.currentUser?.displayName?.let { MessageDto(auth.uid.toString(), authorName = it, chatId, body, remoteRepository.timeToDbFormat()) }
        remoteRepository.sendMessage(messageDto)
    }

    private fun sendNotificationToken() {
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.sendNotificationToken(auth.uid.toString())
        }

    }

    fun loadMessagesFromRoom(hostState: SnackbarHostState,id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.getMessagesFromRoom(hostState, id)
        }
    }
    fun getMessages(): MutableLiveData<MutableList<MessageDto>> {
        return RemoteRepository.messages
    }

    companion object {
        var currentChatId = ""
        fun addToCurrentMessages(msg: MessageDto) {
            val repo = RemoteRepository()
            if (currentChatId == msg.roomId) {
                repo.addToCurrentMessages(msg)
            }
        }
    }

}