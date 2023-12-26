package com.example.foxichat.view_model

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.foxichat.dto.User
import com.example.foxichat.model.RemoteRepository
import com.example.foxichat.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(val auth: FirebaseAuth) : ViewModel() {


    private val remoteRepository = RemoteRepository()


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
            remoteRepository.createUser(scope, hostState, user)
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

    fun signOut() {
        CoroutineScope(Dispatchers.IO).launch {
            auth.signOut()
        }
    }


}