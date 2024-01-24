package com.example.foxichat.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foxichat.presentation.HomeScreen
import com.example.foxichat.presentation.Screens
import com.example.foxichat.presentation.SettingsScreen
import com.example.foxichat.presentation.SpotifyScreen
import com.example.foxichat.presentation.isNavBarVisible
import com.example.foxichat.presentation.view_model.ChatViewModel
import com.example.foxichat.presentation.view_model.SpotifyViewModel
import kotlinx.coroutines.CoroutineScope

enum class Screen {
    HOME,
    SIGNUP,
    SIGNIN,
    CHAT_SCREEN,
    SETTINGS_SCREEN,
    SPOTIFY_SCREEN,
    TEST_SCREEN
}

@Composable
fun NavigationHost(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: ChatViewModel,
    spotifyViewModel: SpotifyViewModel,
    navController: NavHostController
) {
    Log.d("APP_NAV", "WORKEDV")
    val startDest = Screen.SIGNIN.name
    NavHost(
        navController = navController,
        startDestination = startDest,
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

        val screens = Screens(navController, viewModel, spotifyViewModel, snackbarHostState)
        composable(
            route = Screen.HOME.name,

            ) {

            HomeScreen(
                snackbarHostState = snackbarHostState,
                navController = navController,
                viewModel = viewModel,
                spotifyViewModel = spotifyViewModel
            )
        }
        composable(
            route = Screen.SIGNUP.name,

            ) {
            screens.SignUpScreen(scope, snackbarHostState)

        }
        composable(
            Screen.SIGNIN.name,

            ) {
            screens.SignInScreen()
            isNavBarVisible.value = false
        }
        composable(
            Screen.CHAT_SCREEN.name + "/{chat_id}/{chat_name}",
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            val chatId = it.arguments?.getString("chat_id")
            val chatName = it.arguments?.getString("chat_name")
            screens.ChatScreen(chatId, chatName)

        }
        composable(
            Screen.SETTINGS_SCREEN.name,
        ) {
            SettingsScreen(
                snackbarHostState = snackbarHostState,
                navController = navController,
                viewModel = viewModel,
                spotifyViewModel = spotifyViewModel
            )
        }
        composable(
            Screen.SPOTIFY_SCREEN.name,
        ) {
            SpotifyScreen(
                snackbarHostState = snackbarHostState,
                navController = navController,
                viewModel = viewModel,
                spotifyViewModel = spotifyViewModel
            )
        }
        composable(Screen.TEST_SCREEN.name) {
            screens.TestNotificationScreen()
        }
    }
}