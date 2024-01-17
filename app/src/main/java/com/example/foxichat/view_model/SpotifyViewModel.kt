package com.example.foxichat.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foxichat.model.SpotifyRepository
import com.example.foxichat.spotifyAppRemote
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.ListItem
import com.spotify.protocol.types.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SpotifyViewModel : ViewModel(){
 init {
     println("VIEW_MODEL new instance")
 }
    private val repo = SpotifyRepository()
    val currentSongDetails by lazy {
        MutableLiveData<String>()
    }

    val isPlaying by lazy {
        MutableLiveData<Boolean>()
    }

    val currentSongImageUrl by lazy {
        MutableLiveData<ImageUri>()
    }



//    fun connect() {
//
//        val connectionParams = ConnectionParams.Builder(clientId)
//            .setRedirectUri(redirectUri)
//            .showAuthView(true)
//            .build()
//        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
//            override fun onConnected(appRemote: SpotifyAppRemote) {
//                spotifyAppRemote = appRemote
//                Log.d("SPOTIFY_AUTH", "Connected! Yay!")
//                // Now you can start interacting with App Remote
//
//            }
//
//            override fun onFailure(throwable: Throwable) {
//                Log.e("SPOTIFY_AUTH", throwable.message, throwable)
//                // Something went wrong when attempting to connect! Handle errors here
//            }
//        })
//    }
fun connected() {
    spotifyAppRemote?.let {
        // Play a playlist
        //val playlistURI = it.playerApi.resume()
        // it.playerApi.play(playlistURI)
        // Subscribe to PlayerState
        it.playerApi.subscribeToPlayerState().setEventCallback {
            val track: Track = it.track
            currentSongDetails.value = "${track.name} by ${track.artist.name}"
            currentSongImageUrl.value = track.imageUri
            Log.d("PLAYING_TRACK", track.name + " by " + track.artist.name)
        }
        //it.playerApi.pause()

    }

}

    fun previous() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }
    fun next() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun pause() {
        spotifyAppRemote?.let {
            isPlaying.value = false
            it.playerApi.pause()
        }
        connected()
    }
    fun play() {
        spotifyAppRemote?.let {
            isPlaying.value = true
            it.playerApi.resume()
            connected()
        }
    }

    fun loadSpotifyRecommendedContent() {
        CoroutineScope(Dispatchers.IO).launch {
            val res = async {repo.loadSpotifyContent()}
            res.await()
            if (getSpotifyContent().value != null) {
                for (item in getSpotifyContent().value!!.items) {
                    loadSpotifyRecommendedContentChildren(item)
                }
            }
        }


    }
    private fun loadSpotifyRecommendedContentChildren(item: ListItem) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.loadChildren(item)
        }

    }
    fun loadImage(uri: ImageUri) {
        uri.raw?.let { Log.d("SPOTI_", it) }
        CoroutineScope(Dispatchers.IO).launch {
            repo.getImageFromUri(uri)
        }
    }

    fun getImage() = repo.image
    fun getSpotifyContent() = repo.recommendedContent
    fun getSpotifyContentChildren() = repo.recommendedContentChildren
}