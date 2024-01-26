package com.example.foxichat.presentation.view_model

import android.util.Patterns
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.foxichat.AuthenticationWorker
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.RoomDto
import com.example.foxichat.dto.UserDto
import com.example.foxichat.repository.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : ViewModel() {



    companion object {
        private const val WHITESPACE_CHAR = ' '
        const val PASSWORD_LENGTH = 6
    }
    val messages: MutableLiveData<MutableList<MessageDto>> by lazy {
        MutableLiveData<MutableList<MessageDto>>()
    }
    val userRoomListDto: MutableLiveData<List<RoomDto>> by lazy {
        MutableLiveData<List<RoomDto>>()
    }
    val roomsList: MutableLiveData<List<RoomDto>> by lazy {
        MutableLiveData<List<RoomDto>>()
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
    init {
        AuthenticationWorker.authenticate()
        loadUserRooms()
    }

    // *********************** INPUT VALIDATION *******************************

    fun isMessageFromMe(msg: MessageDto): Boolean {
        return msg.authorId == AuthenticationWorker.auth.uid.toString()
    }

    fun validatePasswordField(p: String): Boolean {
        return !p.contains(WHITESPACE_CHAR) && p.length >= PASSWORD_LENGTH
    }

    fun validatePhoneNumberField(s: String): Boolean {
        return if (s.isBlank() || s.contains(WHITESPACE_CHAR)) {
            false
        } else {
            Patterns.PHONE.matcher(s).matches()
        }
    }

    fun validateEmailField(s: String): Boolean {
        return if (s.isBlank() || s.contains(WHITESPACE_CHAR)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(s).matches()
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
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.createUser(nav, scope, hostState, userDto)
        }

    }

    fun createNewRoom(
        hostState: SnackbarHostState,
        name: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.createNewRoom(
                hostState,
                name,
                creatorId = AuthenticationWorker.auth.uid.toString()
            )
        }
    }

    fun signIn(nav: NavHostController, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.signIn(nav, email, password)
        }
    }

    fun loadAllRooms() {
        isAllRoomListReady.value = false
        viewModelScope.launch(Dispatchers.IO) {
            roomsList.postValue(remoteRepository.loadAllRooms {
                isAllRoomListReady.postValue(it)
            })
        }

    }


    fun loadUserRooms() {

        viewModelScope.launch(Dispatchers.IO) {
            isHomeScreenReady.postValue(false)
            userRoomListDto.postValue(remoteRepository.loadUserRooms {
                isHomeScreenReady.postValue(it)
            })
        }
    }

    fun signOut() {
        AuthenticationWorker.auth.signOut()
    }


    fun joinRoom(hostState: SnackbarHostState, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.addUserToRoom(
                hostState = hostState,
                roomId = id,
                uid = AuthenticationWorker.auth.uid.toString()
            )
        }
    }

    fun sendMessage(body: String, chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val messageDto = AuthenticationWorker.auth.currentUser?.displayName?.let {
                MessageDto(
                    "0".repeat(24),
                    AuthenticationWorker.auth.uid.toString(),
                    authorName = it,
                    chatId,
                    body,
                    remoteRepository.timeToDbFormat()
                )
            }
            remoteRepository.sendMessage(messageDto)
        }
    }

    private fun sendNotificationToken() {
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.sendNotificationToken(AuthenticationWorker.auth.uid.toString())
        }
    }

    fun loadMessagesFromRoom(hostState: SnackbarHostState, id: String): MutableLiveData<MutableList<MessageDto>> {
        isChatReady.value = false
        viewModelScope.launch(Dispatchers.IO) {
            messages.postValue(remoteRepository.getMessagesFromRoom(hostState, id) {
                isChatReady.postValue(it)
            })
        }
        return messages
    }

}