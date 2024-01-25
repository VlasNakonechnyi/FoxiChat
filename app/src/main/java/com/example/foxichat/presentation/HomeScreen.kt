package com.example.foxichat.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.foxichat.R
import com.example.foxichat.presentation.view_model.ChatViewModel
import com.example.foxichat.presentation.view_model.SpotifyViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ChatViewModel,
    spotifyViewModel: SpotifyViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadUserRooms()
    }

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    val allRoomsList by viewModel.userRoomListDto.observeAsState(emptyList())
    val isReady by viewModel.isHomeScreenReady.observeAsState(false)
    
    fun refresh() = refreshScope.launch {
        refreshing = true
        val res = async { viewModel.loadUserRooms() }
        res.await()
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)
    GeneralScaffold(
        snackbarHostState = snackbarHostState,
        navController = navController,
        viewModel = viewModel,
        actions = {},
        topAppBarText = stringResource(id = R.string.home_screen_app_bar_text),
        spotifyViewModel = spotifyViewModel
    ) {
        if (isReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(state)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allRoomsList) {
                        RoomInUserRoomsList(
                            viewModel = viewModel,
                            snackbarHostState = snackbarHostState,
                            nav = navController,
                            roomDto = it
                        )
                    }
                }
                PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))

            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

    }
}