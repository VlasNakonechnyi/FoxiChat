package com.example.foxichat.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foxichat.user_interface.Screens
import com.example.foxichat.view_model.ChatViewModel
import kotlinx.coroutines.CoroutineScope

enum class Screen {
    HOME,
    SIGNUP,
    SIGNIN,
    CHAT_SCREEN,
    TEST_SCREEN
}
@Composable
fun NavigationHost(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: ChatViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = viewModel.authUserNotNullDestination(),
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            )
        }
    ) {

        val screens = Screens(navController, viewModel, snackbarHostState )
        composable(Screen.HOME.name) {
            screens.HomeScreen()
        }
        composable(
            route = Screen.SIGNUP.name,
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            }

        ) {
            screens.SignUpScreen(scope, snackbarHostState)
        }
        composable(Screen.SIGNIN.name) {
            screens.SignInScreen()
        }
        composable(Screen.CHAT_SCREEN.name + "/{chat_id}/{chat_name}") {
            val chatId = it.arguments?.getString("chat_id")
            val chatName = it.arguments?.getString("chat_name")
            screens.ChatScreen(chatId, chatName)
        }
        composable(Screen.TEST_SCREEN.name) {
            screens.TestNotificationScreen()
        }
    }
}