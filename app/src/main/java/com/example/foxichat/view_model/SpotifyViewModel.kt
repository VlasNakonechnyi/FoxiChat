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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Time

class SpotifyViewModel : ViewModel(){
 init {
     println("VIEW_MODEL new instance")

 }
    private val repo = SpotifyRepository()
    val currentSongDetails by lazy {
        MutableLiveData<String>()
    }
    companion object {
        private const val SECONDS_IN_MINUTE = 60

        val isPlaying by lazy {
            MutableLiveData<Boolean>()
        }
    }
    val playbackPosition = MutableLiveData<Int>()
    val passedMins by lazy{ MutableLiveData<String>()}
    val trackDuration = MutableLiveData<Int>()

    val currentSongImageUrl by lazy {
        MutableLiveData<String>()
    }

fun connected() {
    spotifyAppRemote?.let {


        it.playerApi.subscribeToPlayerState().setEventCallback {
            val track: Track = it.track
            
            currentSongDetails.value = "${track.name} by ${track.artist.name}"
            isPlaying.value = !it.isPaused
            trackDuration.postValue(track.duration.toInt())
            playbackPosition.postValue(it.playbackPosition.toInt())
            val totalSeconds = (playbackPosition.value ?: 0) / 1000
            val minutes = totalSeconds / SECONDS_IN_MINUTE
            val seconds = totalSeconds % SECONDS_IN_MINUTE
            passedMins.value=(String.format("%02d:%02d", minutes, seconds))
            Log.d("TIME_MINS" ,totalSeconds.toString())
            Log.d("TIME_MINS" ,String.format("%02d:%02d", minutes, seconds))

            loadImage(it.track.imageUri)

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

            it.playerApi.pause()
        }
     //   connected()
    }
    fun play() {
        spotifyAppRemote?.let {

            it.playerApi.resume()
    //        connected()
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