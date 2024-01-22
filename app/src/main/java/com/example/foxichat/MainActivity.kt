package com.example.foxichat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.foxichat.navigation.NavigationHost
import com.example.foxichat.ui.theme.JetpackComposeExTheme
import com.example.foxichat.view_model.ChatViewModel
import com.example.foxichat.view_model.SpotifyViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /* TODO NOTE: Save keys to localProperties */
    private val clientId = "b3eb571fe1634543ba9153b853cf5631"
    /* TODO NOTE: Save keys to gradle, same as base url */
    private val redirectUri = "http://localhost:3000/callback"

    companion object {
        const val FCM_CHANNEL_ID = "FCM_CHANNEL_ID"

    }

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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val config = resources.configuration
        val locale = resources.configuration.locales[0]
        Locale.setDefault(locale)
        config.setLocale(locale)




        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
        val fcmChannel =
            NotificationChannel(FCM_CHANNEL_ID, "FCM_Channel", NotificationManager.IMPORTANCE_HIGH)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(fcmChannel)
        // TODO NOTE: Initialization of BE related properties in the presentation layer is prohibited
        auth = Firebase.auth

        askNotificationPermission()


        setContent {
            val spotifyViewModel by remember {
                mutableStateOf(SpotifyViewModel())
            }
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()
            JetpackComposeExTheme {
                val viewModel = hiltViewModel<ChatViewModel>()
                Surface(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    NavigationHost(scope, snackbarHostState, viewModel, spotifyViewModel, navController)

                }
            }


        }
    }

    override fun onStart() {
        super.onStart()
        trySpotify()
    }



    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
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


/* TODO NOTE: this logic should bu moved to the data layer.
    Repository or DataSource should  handle this, not Activity */
    private fun authenticateSpotify() {
        Log.d("SPOTIFY_AUTH", "AUTHENTICATING")

        val builder =
            AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.TOKEN, redirectUri)

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthorizationClient.openLoginInBrowser(this, request);
    }
    fun trySpotify() {

        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                // TODO NOTE: Initialization of BE related properties in the presentation layer is prohibited
                spotifyAppRemote = appRemote
                Log.d("SPOTIFY_AUTH", "Connected! Yay!")
                //spotifyViewModel.connected()
                // Now you can start interacting with App Remote

            }

            override fun onFailure(throwable: Throwable) {
                authenticateSpotify()
            }
        })
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        Log.d("SPOTIFY_AUTH", "On new intent")
//        val uri = intent.data
//        if (uri != null) {
//            val response = AuthorizationResponse.fromUri(uri)
//
//            when (response.type) {
//                AuthorizationResponse.Type.TOKEN -> {
//                    //trySpotify()
//                }
//                AuthorizationResponse.Type.ERROR -> {
//                    Log.d("SPOTIFY_AUTH", "Failed")
//                }
//                else -> {
//                    Log.d("SPOTIFY_AUTH", "Failed")
//                }
//            }
//        }
//    }
}









