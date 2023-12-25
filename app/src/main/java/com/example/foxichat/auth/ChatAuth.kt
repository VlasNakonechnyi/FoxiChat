package com.example.foxichat.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object ChatAuth {
    lateinit var auth: FirebaseAuth
    var isSignInSuccessful = true

    fun completeAuth() {
        auth = Firebase.auth
    }

    fun signIn(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   val user = auth.currentUser
                    Log.d("USER_SIGNED_IN", user?.uid.toString())
                    isSignInSuccessful = true
                } else {
                    isSignInSuccessful = false
                    // If sign in fails, display a message to the user.
                    //Log.d("USER_SIGNED_IN", user?.uid.toString())
                }
            }

    }

}