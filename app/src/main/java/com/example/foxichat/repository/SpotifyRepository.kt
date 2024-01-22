package com.example.foxichat.repository

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.foxichat.spotifyAppRemote
import com.spotify.android.appremote.api.ContentApi
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.ListItem
import com.spotify.protocol.types.ListItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpotifyRepository {
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


    fun loadSpotifyContent() {
        spotifyAppRemote?.contentApi?.getRecommendedContentItems(ContentApi.ContentType.DEFAULT)
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

        spotifyAppRemote?.contentApi?.getChildrenOfItem(item, toIndex, 0)?.setResultCallback {
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
            val bitmap = spotifyAppRemote?.imagesApi?.getImage(uri)
            bitmap?.setResultCallback { bitmap ->
                image.value = bitmap
                Log.d("SPOTI_", "SUCCESS")
            }?.setErrorCallback { throwable ->
                Log.d("SPOTI_ERROR", throwable.message.toString())
            }
        }
    }
}