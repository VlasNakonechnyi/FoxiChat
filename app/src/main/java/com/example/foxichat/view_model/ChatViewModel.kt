package com.example.foxichat.view_model

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.foxichat.auth
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.UserDto
import com.example.foxichat.repository.RemoteRepository
import com.example.foxichat.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val remoteRepository : RemoteRepository
) : ViewModel() {
        // TODO NOTE: Passing context (fragment or activity) can lead to memory leaks. Avoid passing
        //  short-lived contexts to the classes whose instances have a longer lifespan in memory

    companion object {
        const val PASSWORD_LENGTH = 6
    }



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
            // TODO NOTE: add "import android.util.Patterns" to te imports and clean up the code here
            android.util.Patterns.PHONE.matcher(s).matches()
        }
    }

    fun validateEmailField(s: String): Boolean {
        return if (s.isBlank() || s.contains(' ')) {
            false
        } else {
            // TODO NOTE: add "import android.util.Patterns" to te imports and clean up the code here
            android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()
        }
    }
    // ******************* FUNCTIONALITY ***************************

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
        // TODO NOTE: Use viewModelScope with the required dispatcher to launch a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.createNewRoom(
                hostState,
                name,
                creatorId = auth.uid.toString()
            )
        }

    }

    fun signIn(nav: NavHostController, email: String, password: String) {

        /* TODO NOTE: FirebaseAuth is BE in this app.
            1. Use data layer (repository or datasource) to communicate with BE
            ViewModels purpose is to handle logic related to UI. ViewModels can't communicate with
            BE directly.
            2. ViewModel calls the methods from the repository (or other datasource) and this data
            layer classes communicate with BE or database. So signIn() in viewModel should look like
                private val _isSignedIn: MutableLiveData<Boolean> = MutableLiveData()
                fun signIn(email: String, password: String) {
                    viewModelScope.launch(Dispatchers.IO) {
                    _isSignedIn.postValue(repository.signIn(email, password)).
                }
            }
            3. Every request to BE, database etc should be done in separate coroutine
            Use viewModelScope to launch the coroutine inside view models */
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
                /* TODO NOTE: If you update the livedata in a background thread and this live data
                    is observed by UI on main thread, there are potential issues of updating UI.
                    To avoid them use liveData.postValue(). This ensures that the UI will be updated
                    on the main thread */
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

}