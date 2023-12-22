package com.example.foxichat.view_model
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.foxichat.entity.User
import com.example.foxichat.model.RemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {


    private val remoteRepository = RemoteRepository()
    companion object {
        var currentUser: User? = null
    }

    // *********************** INPUT VALIDATION *******************************

    fun validatePasswordField(p: String) : Boolean{
        return !p.contains(' ') && p.length >= 6
    }
    fun validatePhoneNumberField(s: String) : Boolean{
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

    fun addNewUser(nav: NavHostController, email: String, password: String, username: String) {
//        val email: String,
//        val displayName: String,
//        val phoneNumber: String,
//        val password: String,
//        val photoUrl: String
        val user = User(
            email,
            username,
            "0978443213",
            password,
            ""
        )
        CoroutineScope(Dispatchers.IO).launch{
            remoteRepository.createUser(user)
        }
    }




}