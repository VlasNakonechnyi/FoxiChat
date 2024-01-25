package com.example.foxichat.repository

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.foxichat.SpotifyWorker
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.ContentApi
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.ListItem
import com.spotify.protocol.types.ListItems
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpotifyRepository (private val application: Application) {
    private val toIndex = 15
    val recommendedContent by lazy {
        MutableLiveData<ListItems>()
    }
    val recommendedContentChildren by lazy {
        MutableLiveData<MutableMap<ListItem, ListItems>>()
    }
    val image by lazy {
        MutableLiveData<Bitmap>()
    }

    companion object {
        // private const val SECONDS_IN_MINUTE = 60
        /* TODO NOTE: Save keys to localProperties */
        private val clientId = "b3eb571fe1634543ba9153b853cf5631"
        /* TODO NOTE: Save keys to gradle, same as base url */
        private val redirectUri = "http://localhost:3000/callback"

    }

    private fun authenticateSpotify() {
        Log.d("SPOTIFY_AUTH", "AUTHENTICATING")

        val builder =
            AuthorizationRequest.Builder(
                clientId, AuthorizationResponse.Type.TOKEN,
                redirectUri
            )

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        //AuthorizationClient.openLoginInBrowser(application.applicationContext, request);
    }
    fun tryConnectingToSpotify(callback : (Boolean) -> Unit) {

        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(application.applicationContext, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                // TODO NOTE: Initialization of BE related properties in the presentation layer is prohibited
                SpotifyWorker.authenticateSpotify(appRemote)
                Log.d("SPOTIFY_AUTH", "Connected! Yay!")
                callback(true)

            }

            override fun onFailure(throwable: Throwable) {
                authenticateSpotify()
                callback(false)
            }
        })
    }



    fun loadSpotifyContent() {
        SpotifyWorker.spotifyAppRemote?.contentApi?.getRecommendedContentItems(ContentApi.ContentType.DEFAULT)
            ?.setResultCallback { listItems ->
                recommendedContent.value = listItems

            }?.setErrorCallback { throwable ->
                throw throwable
            }
    }
    fun loadChildren(item: ListItem) {
       // println(recommendedContentChildren.value?.items.contentToString() + "1")
        CoroutineScope(Dispatchers.Main).launch {
            if (recommendedContentChildren.value == null) recommendedContentChildren.value = mutableMapOf()
        }

        SpotifyWorker.spotifyAppRemote?.contentApi?.getChildrenOfItem(item, toIndex, 0)?.setResultCallback {
            recommendedContentChildren.value?.set(item, it)
            Log.d("LOADING_CHILDREN", recommendedContentChildren.value.toString())
        }?.setErrorCallback { throwable ->
            throw throwable
        }
    }

    fun getImageFromUri(uri: ImageUri) {
        uri.raw?.let { Log.d("SPOTI_", it) }
        if (uri.raw?.isNotBlank()!!) {
            Log.d("SPOTI_", "LOADING IMAGE")
            val bitmap = SpotifyWorker.spotifyAppRemote?.imagesApi?.getImage(uri)
            bitmap?.setResultCallback { bitmap ->
                image.value = bitmap
                Log.d("SPOTI_", "SUCCESS")
            }?.setErrorCallback { throwable ->
                Log.d("SPOTI_ERROR", throwable.message.toString())
            }
        }
    }
}