package com.example.foxichat.user_interface

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.foxichat.R
import com.example.foxichat.navigation.BottomNavItems
import com.example.foxichat.spotifyAppRemote
import com.example.foxichat.view_model.ChatViewModel
import com.example.foxichat.view_model.SpotifyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val isNavBarVisible by lazy {
    MutableLiveData<Boolean>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralScaffold(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ChatViewModel,
    spotifyViewModel: SpotifyViewModel,
    topAppBarText: String,
    actions: @Composable () -> Unit,
    body: @Composable () -> Unit,
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var openAlertDialog by remember { mutableStateOf(false) }

    val allRooms by viewModel.getAllRooms().observeAsState(listOf())
    val currentSongDetails by spotifyViewModel.currentSongDetails.observeAsState("")
    val isPlaying by spotifyViewModel.isPlaying.observeAsState(false)

    when {
        openAlertDialog -> {
            CreateRoomAlertDialog(
                onDismissRequest = {
                    openAlertDialog = false
                    showBottomSheet = false
                },
                onConfirmation = { name ->

                    viewModel.createNewRoom(snackbarHostState, name = name)
                    openAlertDialog = false
                    showBottomSheet = false


                },
                dialogTitle = stringResource(id = R.string.text_button_create_room),
                icon = Icons.Outlined.Add
            )
        }

        else -> {}
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                        titleContentColor = MaterialTheme.colorScheme.secondary,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    title = {
                        Text(
                            topAppBarText,
                        )
                    },

                    actions = {


                    }
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp)),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            currentSongDetails?.let {
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(
                                onClick = { }
                            ) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(id = R.drawable.music),
                                    contentDescription = ""
                                )
                            }
                            IconButton(onClick = { spotifyViewModel.previous() }) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(id = R.drawable.back),
                                    contentDescription = ""
                                )
                            }
                            IconButton(onClick = {
                                if (isPlaying) spotifyViewModel.pause() else spotifyViewModel.play()
                            }
                            ) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    painter = if (isPlaying) painterResource(id = R.drawable.pause) else painterResource(
                                        id = R.drawable.play
                                    ),
                                    contentDescription = ""
                                )
                            }
                            IconButton(onClick = { spotifyViewModel.next() }) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(id = R.drawable.next),
                                    contentDescription = ""
                                )
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(id = R.drawable.shuffle),
                                    contentDescription = ""
                                )
                            }

                        }
                    }
                }

            }

        },
        floatingActionButton = {

            FloatingActionButton(
                onClick = {

                    viewModel.loadAllRooms()
                    showBottomSheet = true

                },
                shape = CircleShape,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Outlined.Add, "Localized description")
            }

        },
        bottomBar = {

            NavigationBar(
                modifier = Modifier.clip(CircleShape)
                //backgroundColor = MaterialTheme.colorScheme.primary
               // containerColor = MaterialTheme.colorScheme.primary,

                ) {

                BottomNavItems.bottomNavItems.forEach {
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == it.route,
                        icon = it.icon,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.secondary
                        ),
                        onClick = {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true

                        }
                    })
                }
            }


        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            body()
            if (showBottomSheet) {
                ModalBottomSheet(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    // Sheet content
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TextButton(onClick = {
                                CoroutineScope(Dispatchers.Main).launch { sheetState.hide() }
                                    .invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                            }
                            ) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                            Button(onClick = {
                                openAlertDialog = true

                            }) {
                                Text(text = stringResource(id = R.string.text_button_create_room))
                            }

                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(allRooms) {
                                RoomInJoinRoomList(
                                    room = it,
                                    viewModel = viewModel,
                                    nav = navController,
                                    snackbarHostState = snackbarHostState
                                )
                            }
                        }


                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedSpotifyIcon() {

    val transition = rememberInfiniteTransition()
    val scale by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(

            animation = tween(1000),

            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Icon(
        painter = painterResource(
            id = R.drawable.spotify
        ),
        contentDescription = "",
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
    )


}