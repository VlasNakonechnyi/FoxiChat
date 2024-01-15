package com.example.foxichat.view_model

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.example.foxichat.auth
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.UserDto
import com.example.foxichat.model.RemoteRepository
import com.example.foxichat.navigation.Screen
import com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatViewModel(private val application: Application) :
    AndroidViewModel(application) {
    private val remoteRepository = RemoteRepository(application.applicationContext)

    companion object {

        const val PASSWORD_LENGTH = 6


    }
    //   private val remoteRepository = RemoteRepository()


    val isChatReady by lazy {
        MutableLiveData<Boolean>()
    }
    val isHomeScreenReady by lazy {
        MutableLiveData<Boolean>()
    }
    val isAllRoomListReady by lazy {
        MutableLiveData<Boolean>()
    }


    // *********************** INPUT VALIDATION *******************************

    fun isMessageFromMe(msg: MessageDto): Boolean {
        return msg.authorId == auth.uid.toString()
    }

    fun validatePasswordField(p: String): Boolean {
        return !p.contains(' ') && p.length >= PASSWORD_LENGTH
    }

    fun validatePhoneNumberField(s: String): Boolean {
        return if (s.isBlank() || s.contains(' ')) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(s).matches()
        }
    }

    fun validateEmailField(s: String): Boolean {
        return if (s.isBlank() || s.contains(' ')) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()
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
        hostState: SnackbarHostState,

        name: String
    ) {
        println("CREATE NEW ROOM WORKED")
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.createNewRoom(
                hostState,
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

    fun loadAllRooms() {
        isAllRoomListReady.value = false

        remoteRepository.loadAllRooms {
            isAllRoomListReady.value = true
        }

    }


    fun loadUserRooms() {
        isHomeScreenReady.value = false

        remoteRepository.loadUserRooms {
            isHomeScreenReady.value = true
        }
    }

    fun signOut() {
        auth.signOut()
    }


    fun joinRoom(hostState: SnackbarHostState, id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.addUserToRoom(
                hostState = hostState,
                roomId = id,
                uid = auth.uid.toString()
            )
        }
    }

    fun sendMessage(body: String, chatId: String) {
        println(auth.currentUser?.displayName)
        val messageDto = auth.currentUser?.displayName?.let {
            MessageDto(
                "0".repeat(24),
                auth.uid.toString(),
                authorName = it,
                chatId,
                body,
                remoteRepository.timeToDbFormat()
            )
        }
        remoteRepository.sendMessage(messageDto)
    }

    private fun sendNotificationToken() {
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.sendNotificationToken(auth.uid.toString())
        }

    }

    fun loadMessagesFromRoom(hostState: SnackbarHostState, id: String) {
        isChatReady.value = false
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.getMessagesFromRoom(hostState, id) {
                isChatReady.value = it
            }
        }
    }

    fun getMessages(): MutableLiveData<MutableList<MessageDto>> {
        return RemoteRepository.messages
    }

    fun getUserRooms(): MutableLiveData<List<Room>> {
        return remoteRepository.userRoomList
    }

    fun getAllRooms(): MutableLiveData<List<Room>> {
        return remoteRepository.roomsList

    }

    // *************************** SPOTIFY *****************************



}