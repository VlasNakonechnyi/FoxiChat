package com.example.foxichat.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foxichat.user_interface.Screens
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
            isNavBarVisible.value = true
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
            isNavBarVisible.value = false
        }
        composable(Screen.SIGNIN.name) {
            screens.SignInScreen()
            isNavBarVisible.value = false
        }
        composable(Screen.CHAT_SCREEN.name + "/{chat_id}/{chat_name}") {
            val chatId = it.arguments?.getString("chat_id")
            val chatName = it.arguments?.getString("chat_name")
            screens.ChatScreen(chatId, chatName)
            isNavBarVisible.value = false
        }
        composable(Screen.SETTINGS_SCREEN.name) {
            screens.SettingsScreen()
            isNavBarVisible.value = true
        }
        composable(Screen.TEST_SCREEN.name) {
            screens.TestNotificationScreen()
        }
    }
}