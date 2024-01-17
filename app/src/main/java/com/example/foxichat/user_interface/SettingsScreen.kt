package com.example.foxichat.user_interface

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.foxichat.R
import com.example.foxichat.view_model.ChatViewModel
import com.example.foxichat.view_model.SpotifyViewModel
import com.github.orioneee.ColorMode
import com.github.orioneee.Ctm
import com.github.orioneee.ThemeMode
import kotlin.math.min

@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ChatViewModel,
    spotifyViewModel: SpotifyViewModel
) {
    LaunchedEffect(Unit) {
        spotifyViewModel.connected()
    }
    GeneralScaffold(
        snackbarHostState = snackbarHostState,
        navController = navController,
        viewModel = viewModel,
        actions = {},
        topAppBarText = stringResource(id = R.string.settings_app_bar_text),
        spotifyViewModel = spotifyViewModel
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.text_themes),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary

            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyVerticalStaggeredGrid(

                columns = StaggeredGridCells.Adaptive(70.dp),
                verticalItemSpacing = 20.dp,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(Ctm.AllColorModes) {
                    ColorMode(cm = it)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            LightDarkModeSwitch()
        }
    }

}
@Composable
fun LightDarkModeSwitch() {

    var checked by remember { mutableStateOf( Ctm.currentThemeMode.value == ThemeMode.Light) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
        },
        thumbContent = if (checked) {
            {
                Icon(
                    painter = painterResource(id = R.drawable.outline_wb_sunny_24),
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
                Ctm.setThemeMode(ThemeMode.Light)
            }
        } else {
            {
                Icon(
                    painter = painterResource(id = R.drawable.outline_dark_mode_24),
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
                Ctm.setThemeMode(ThemeMode.Dark)
            }
        }
    )
}
@Composable
fun ColorMode(cm: ColorMode) {
    Surface(
        modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .clickable { Ctm.setColorMode(cm) },
        color = cm.theme.light.primary
    ) {
        val colors = listOf(
            cm.theme.light.primary,
            cm.theme.light.secondary,
            cm.theme.light.tertiary,
            cm.theme.light.background
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val (width, height) = size
            val radius = min(width, height) / 2
            val center = Offset(width / 2, height / 2)

            // Drawing each quarter
            colors.forEachIndexed { index, color ->
                drawArc(
                    color = color,
                    startAngle = 90f * index,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
        }

    }
}