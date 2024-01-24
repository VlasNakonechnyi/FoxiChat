package com.example.foxichat

import com.spotify.android.appremote.api.SpotifyAppRemote

object SpotifyWorker {
    var spotifyAppRemote: SpotifyAppRemote? = null

    fun authenticateSpotify(app: SpotifyAppRemote) {
        spotifyAppRemote = app
    }
}
