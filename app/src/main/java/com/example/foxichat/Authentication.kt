package com.example.foxichat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthenticationWorker {
    lateinit var auth: FirebaseAuth

    fun authenticate() {
        auth = Firebase.auth
    }
}
