package com.example.foxichat.view_model

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.User
import com.example.foxichat.model.RemoteRepository
import com.example.foxichat.model.RoomsDatabase
import com.example.foxichat.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel(val auth: FirebaseAuth, application: Application) :
    AndroidViewModel(application) {


    private val remoteRepository = RemoteRepository()

    var roomsList = mutableStateListOf<Room>()
    var userRoomList = mutableStateListOf<Room>()


    // *********************** INPUT VALIDATION *******************************

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

    fun addNewUser(
        nav: NavHostController,
        email: String,
        password: String,
        username: String,
        phone: String,
        hostState: SnackbarHostState,
        scope: CoroutineScope
    ) {

        val user = User(
            email,
            username,
            phone,
            password,
            "nothing"
        )
        CoroutineScope(Dispatchers.IO).launch {
            remoteRepository.createUser(nav, scope, hostState, user)
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
            remoteRepository.createNewRoom(nav, hostState, scope, name, creatorId = "sds")
        }

    }

    fun signIn(nav: NavHostController, email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("USER_SIGNED_IN", user?.uid.toString())
                    nav.navigate(Screen.HOME.name)
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
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    response.body()?.string()?.let {
                        val rooms = gson.fromJson(it, Array<Room>::class.java).asList()
                        Log.d("", rooms.toString())
                        CoroutineScope(Dispatchers.IO).launch{
                            insertRoomsToLocalDb(rooms)
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // TODO
                }
            })
            val res = async { getRoomsFromLocalDb() }
            roomsList = res.await()
        }

    }
    fun loadUserRooms() {
        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            remoteRepository.api.getUserRooms(auth.uid.toString()).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    response.body()?.string()?.let {
                        val rooms = gson.fromJson(it, Array<Room>::class.java).asList()
                        Log.d("", rooms.toString())
                        CoroutineScope(Dispatchers.IO).launch{
                            insertRoomsToLocalDb(rooms)
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // TODO
                }
            })
            val res = async { getRoomsFromLocalDb() }
            roomsList = res.await()
        }

    }

    fun signOut() {
        CoroutineScope(Dispatchers.IO).launch {
            auth.signOut()
        }
    }

    private suspend fun insertRoomsToLocalDb(rooms: List<Room>) {
        //RoomsDatabase(getApplication()).roomDao().deleteAllRooms()
        Log.d("CHAT_VIEW_MODEL", rooms.toString())
        RoomsDatabase(getApplication()).roomDao().insertRooms(rooms)
    }

    private suspend fun getRoomsFromLocalDb(): SnapshotStateList<Room> {
      //RoomsDatabase(getApplication()).roomDao().deleteAllRooms()
        Log.d("FROM LOCAL DB", RoomsDatabase(getApplication()).roomDao().getAllRooms().toString())
        return RoomsDatabase(getApplication()).roomDao().getAllRooms().toMutableStateList()
    }


}