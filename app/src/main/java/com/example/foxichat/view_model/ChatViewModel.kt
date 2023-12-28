package com.example.foxichat.view_model

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel(val auth: FirebaseAuth, application: Application) :
    AndroidViewModel(application) {


    private val remoteRepository = RemoteRepository()
    private val roomsList: MutableLiveData<List<Room>> by lazy {
        MutableLiveData<List<Room>>()
    }


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
//        val email: String,
//        val displayName: String,
//        val phoneNumber: String,
//        val password: String,
//        val photoUrl: String
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

    fun loadAllRooms() {
        var gson = Gson()
        remoteRepository.api.getAllRooms().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.body()?.string()?.let {

                    val rooms = gson.fromJson(it, Array<Room>::class.java).asList()


                    Log.d("", rooms.toString())
                    Log.d("CHAT_VIEW_MODEL", roomsList.toString())
                    CoroutineScope(Dispatchers.IO).launch {

                        insertRoomsToLocalDb(rooms)

                    }


                }

                // insertRoomsToLocalDb()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    fun signOut() {
        CoroutineScope(Dispatchers.IO).launch {
            auth.signOut()
        }
    }

    private suspend fun insertRoomsToLocalDb(rooms: List<Room>) {
        Log.d("CHAT_VIEW_MODEL", rooms.toString())

        RoomsDatabase(getApplication()).roomDao().insertRooms(rooms)
        Log.d("CHAT_VIEW_MODEL", RoomsDatabase(getApplication()).roomDao().getAllRooms().toString())

    }


}