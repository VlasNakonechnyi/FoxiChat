package com.example.foxichat.navigation

import android.util.Log
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foxichat.R
import com.example.foxichat.user_interface.GeneralScaffold
import com.example.foxichat.user_interface.HomeScreen
import com.example.foxichat.user_interface.RoomInJoinRoomList
import com.example.foxichat.user_interface.Screens
import com.example.foxichat.user_interface.SettingsScreen
import com.example.foxichat.user_interface.isNavBarVisible
import com.example.foxichat.view_model.ChatViewModel
import kotlinx.coroutines.CoroutineScope

enum class Screen {
    HOME,
    SIGNUP,
    SIGNIN,
    CHAT_SCREEN,
    SETTINGS_SCREEN,
    TEST_SCREEN
}

@Composable
fun NavigationHost(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: ChatViewModel,
    navController: NavHostController
) {
    Log.d("APP_NAV", "WORKEDV")
    val startDest = Screen.SIGNIN.name
    NavHost(
        navController = navController,
        startDestination = startDest,

        ) {

        val screens = Screens(navController, viewModel, snackbarHostState)
        composable(
            route = Screen.HOME.name,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(100, easing = EaseIn)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(100, easing = EaseIn)
                )
            }
        ) {

            HomeScreen(
                snackbarHostState = snackbarHostState,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(
            route = Screen.SIGNUP.name,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = EaseIn)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = EaseIn)
                )
            }
            ) {
            screens.SignUpScreen(scope, snackbarHostState)

        }
        composable(
            Screen.SIGNIN.name,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = EaseIn)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = EaseIn)
                )
            }
        ) {
            screens.SignInScreen()
            isNavBarVisible.value = false
        }
        composable(Screen.CHAT_SCREEN.name + "/{chat_id}/{chat_name}",
            enterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = EaseIn)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = EaseIn)
                )
            }) {
            val chatId = it.arguments?.getString("chat_id")
            val chatName = it.arguments?.getString("chat_name")
            screens.ChatScreen(chatId, chatName)

        }
        composable(Screen.SETTINGS_SCREEN.name,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = EaseIn)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = EaseIn)
                )
            }) {
            SettingsScreen(
                snackbarHostState = snackbarHostState,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(Screen.TEST_SCREEN.name) {
            screens.TestNotificationScreen()
        }
    }
}