package com.example.foxichat.user_interface

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.foxichat.R
import com.example.foxichat.view_model.ChatViewModel
import com.github.orioneee.ColorMode
import com.github.orioneee.Ctm

@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ChatViewModel
) {
    GeneralScaffold(
        snackbarHostState =snackbarHostState,
        navController = navController,
        viewModel = viewModel,
        actions = {},
        topAppBarText = stringResource(id = R.string.settings_app_bar_text)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Themes")
            LazyVerticalStaggeredGrid(

                columns = StaggeredGridCells.Adaptive(50.dp),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(Ctm.AllColorModes) {
                    ColorMode(cm = it)
                }
            }
        }
    }

}
@Composable
fun ColorMode(cm: ColorMode) {
    Surface(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .clickable { Ctm.setColorMode(cm) },
        color = cm.theme.light.primary
    ) {}
}