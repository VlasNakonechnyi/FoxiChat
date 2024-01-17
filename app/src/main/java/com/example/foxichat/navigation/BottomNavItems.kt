package com.example.foxichat.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foxichat.user_interface.AnimatedSpotifyIcon

sealed class BottomNavItems( val route: String, val icon: @Composable () -> Unit) {
    companion object {
        val bottomNavItems = listOf(Settings, Home, Spotify)
    }
    data object Settings : BottomNavItems(
        route = Screen.SETTINGS_SCREEN.name,
        {
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "",
            modifier = Modifier.size(30.dp)
        )
    })
    data object Home : BottomNavItems(route = Screen.HOME.name, {
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = "",
            modifier = Modifier.size(30.dp)
        )
    })
    //ToDo
    data object Spotify : BottomNavItems(route = Screen.SPOTIFY_SCREEN.name, {
        AnimatedSpotifyIcon()
    })

}
