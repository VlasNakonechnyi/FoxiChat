package com.example.foxichat

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foxichat.navigation.Screen
import com.example.foxichat.ui.theme.JetpackComposeExTheme
import com.example.foxichat.user_interface.Screens
import com.example.foxichat.view_model.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {
    lateinit var auth: FirebaseAuth
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val viewModel = ChatViewModel(auth)

        askNotificationPermission()
        setContent {
            JetpackComposeExTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val scope = rememberCoroutineScope()
                    val snackbarHostState = remember { SnackbarHostState() }
                    Scaffold (
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState)}
                    ) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(it)) {
                            NavigationHost(scope, snackbarHostState, viewModel)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NavigationHost(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        viewModel: ChatViewModel
    ) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = if (auth.currentUser == null) Screen.SIGNIN.name else Screen.HOME.name,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        1000, easing = LinearEasing
                    )
                )
            }
        ) {

            val screens = Screens(navController, viewModel)
            composable(Screen.HOME.name) {
                screens.HomeScreen()
            }
            composable(
                route = Screen.SIGNUP.name,
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            100, easing = LinearEasing
                        )
                    )
                }

            ) {
                screens.SignUpScreen(scope, snackbarHostState)
            }
            composable(Screen.SIGNIN.name) {
                screens.SignInScreen()
            }
            composable(Screen.CHAT_SCREEN.name) {
                screens.ChatScreen()
            }
            composable(Screen.TEST_SCREEN.name) {
                screens.TestNotificationScreen()
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}









