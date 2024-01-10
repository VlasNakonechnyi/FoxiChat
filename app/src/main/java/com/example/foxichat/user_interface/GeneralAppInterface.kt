package com.example.foxichat.user_interface

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.example.foxichat.navigation.BottomNavItems
import com.example.foxichat.navigation.NavigationHost
import com.example.foxichat.navigation.Screen
import com.example.foxichat.view_model.ChatViewModel
import kotlinx.coroutines.launch

val isNavBarVisible by lazy {
    MutableLiveData<Boolean>()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralScaffold(navController: NavHostController, viewModel: ChatViewModel) {
    isNavBarVisible.value = true
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val allRoomsList by viewModel.roomsList.observeAsState()
    val screens = Screens(navController, viewModel, snackbarHostState)
    var openAlertDialog by remember { mutableStateOf(false) }
    var isBarVisible = isNavBarVisible.observeAsState()

    when {
        openAlertDialog -> {
            screens.CreateRoomAlertDialog(
                onDismissRequest = {
                    openAlertDialog = false
                    showBottomSheet = false
                },
                onConfirmation = { name ->

                    viewModel.createNewRoom(snackbarHostState, scope, name = name)
                    openAlertDialog = false
                    showBottomSheet = false
                    viewModel.loadUserRooms()

                },
                dialogTitle = "Create new room",
                icon = Icons.Outlined.Add
            )
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AnimatedVisibility(
                visible = navController.currentDestination?.route != Screen.SIGNIN.name &&
                        navController.currentDestination?.route != Screen.SIGNUP.name
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
                            "Chats",
                        )
                    },

                    actions = {

                    }

                )
            }
        },

        floatingActionButton = {

            isBarVisible.value?.let {
                AnimatedVisibility(it) {
                    FloatingActionButton(
                        onClick = {

                            viewModel.getAllRooms()
                            showBottomSheet = true

                        },
                        shape = CircleShape,
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Outlined.Add, "Localized description")
                    }
                }
            }


        },
        bottomBar = {

            isBarVisible.value?.let {
                AnimatedVisibility(visible = it) {

                    NavigationBar(
                        modifier = Modifier

                            .clip(RoundedCornerShape(30.dp))
                            .height(50.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            BottomNavItems.bottomNavItems.forEach {
                                IconButton(onClick = {
                                    navController.navigate(it.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true

                                    }
                                }) {
                                    it.icon()
                                }
                            }
                        }

                    }
                }
            }



        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            println(it.calculateBottomPadding())
            NavigationHost(scope, snackbarHostState, viewModel, navController)
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
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            }
                            ) {
                                Text(text = "Cancel")
                            }
                            Button(onClick = {
                                openAlertDialog = true

                            }) {
                                Text(text = "Create new room")
                            }

                        }

                        LazyColumn(
                            modifier = androidx.compose.ui.Modifier.padding(it)
                        ) {
                            if (allRoomsList != null) {
                                items(items = allRoomsList!!) {
                                    screens.RoomInJoinRoomList(room = it)
                                }
                            }

                        }
                    }

                }
            }
        }

    }
}