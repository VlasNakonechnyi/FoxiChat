package com.example.foxichat.presentation.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foxichat.SpotifyWorker
import com.example.foxichat.repository.SpotifyRepository
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.ListItem
import com.spotify.protocol.types.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpotifyViewModel @Inject constructor(
    private val repo : SpotifyRepository
) : ViewModel() {
    init {
        println("VIEW_MODEL new instance")

    }
    companion object {
        val isPlaying by lazy {
            MutableLiveData<Boolean>()
        }
    }

    val playbackPosition = MutableLiveData<Int>()
    val passedMins by lazy { MutableLiveData<String>() }
    val trackDuration = MutableLiveData<Int>()

    val currentSongDetails by lazy {
        MutableLiveData<String>()
    }

    val currentSongImageUrl by lazy {
        MutableLiveData<String>()
    }




    private fun authenticateSpotify() {

    }

    fun trySpotify() {


    }



    fun previous() {
        SpotifyWorker.spotifyAppRemote?.playerApi?.skipPrevious()
    }

    fun next() {
        SpotifyWorker.spotifyAppRemote?.playerApi?.skipNext()
    }

    fun pause() {
        SpotifyWorker.spotifyAppRemote?.playerApi?.pause()
        //   connected()
    }

    fun play() {
        SpotifyWorker.spotifyAppRemote?.playerApi?.resume()
    }

    fun loadSpotifyRecommendedContent() {

    }

    private fun loadSpotifyRecommendedContentChildren(item: ListItem) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.loadChildren(item)
        }

    }
    fun connected() {
        SpotifyWorker.spotifyAppRemote?.let { it ->
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                currentSongDetails.value = "${track.name} by ${track.artist.name}"
                isPlaying.value = !it.isPaused
                trackDuration.postValue(track.duration.toInt())
                playbackPosition.postValue(it.playbackPosition.toInt())
                loadImage(it.track.imageUri)

                Log.d("PLAYING_TRACK", track.name + " by " + track.artist.name)

            }

        }



    }
    private fun loadImage(uri: ImageUri) {

        CoroutineScope(Dispatchers.IO).launch {
            repo.getImageFromUri(uri)
        }
    }

    fun getImage() = repo.image
    fun getSpotifyContent() = repo.recommendedContent
    fun getSpotifyContentChildren() = repo.recommendedContentChildren
}