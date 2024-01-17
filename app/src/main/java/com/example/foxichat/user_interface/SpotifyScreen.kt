package com.example.foxichat.user_interface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.foxichat.R
import com.example.foxichat.view_model.ChatViewModel
import com.example.foxichat.view_model.SpotifyViewModel
import com.spotify.protocol.types.ListItem

@Composable
fun SpotifyScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ChatViewModel,
    spotifyViewModel: SpotifyViewModel
) {

    val recommendedContent by spotifyViewModel.getSpotifyContent().observeAsState()

    LaunchedEffect(Unit) {
        spotifyViewModel.loadSpotifyRecommendedContent()
        spotifyViewModel.connected()
    }

    GeneralScaffold(
        snackbarHostState = snackbarHostState,
        navController = navController,
        viewModel = viewModel,
        actions = {},
        topAppBarText = stringResource(id = R.string.spotify),
        spotifyViewModel = spotifyViewModel
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
            //.horizontalScroll(enabled = true, state = ScrollState(0))
        ) {
            if (recommendedContent != null) {
                items(recommendedContent!!.items) {

                    ContentItem(viewModel = spotifyViewModel, item = it)
                }
            }
        }
    }


}

@Composable
fun ContentItem(viewModel: SpotifyViewModel, item: ListItem) {


    val children by viewModel.getSpotifyContentChildren().observeAsState()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = item.title, style = MaterialTheme.typography.displaySmall)
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            children?.let {
                it[item]?.let { it1 ->
                    items(it1.items) { child ->
                        ChildContent(viewModel = viewModel, item = child)
                    }
                }
            }
        }
    }


}





@Composable
fun ChildContent(viewModel: SpotifyViewModel, item: ListItem) {


    Box(
        modifier = Modifier
            .width(150.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(30.dp)),

        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(model = item.imageUri.raw, contentDescription = "", contentScale = ContentScale.Crop, modifier = Modifier.shadow(3.dp))
            Text(text = item.title)

        }


    }
}


