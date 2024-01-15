package com.example.foxichat.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.example.foxichat.user_interface.AnimatedSpotifyIcon

sealed class BottomNavItems( val route: String, val icon: @Composable () -> Unit) {
    companion object {
        val bottomNavItems = listOf(Settings, Home, Spotify)
    }
    data object Settings : BottomNavItems(route = Screen.SETTINGS_SCREEN.name, {
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = ""
        )
    })
    data object Home : BottomNavItems(route = Screen.HOME.name, {
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = ""
        )
    })
    //ToDo
    data object Spotify : BottomNavItems(route = "", {
        AnimatedSpotifyIcon()
    })

}
