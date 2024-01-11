package com.example.foxichat.user_interface

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foxichat.R
import com.example.foxichat.dto.Room
import com.example.foxichat.navigation.Screen
import com.example.foxichat.view_model.ChatViewModel

@Composable
fun RoomInUserRoomsList(
    nav: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: ChatViewModel,
    room: Room
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(100.dp)
            .shadow(0.5.dp)
            .clickable(onClick = {
                viewModel.loadMessagesFromRoom(snackbarHostState, room.id)
                nav.navigate(Screen.CHAT_SCREEN.name + "/${room.id}/${room.name}")
            }),
        contentAlignment = Alignment.CenterStart

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.logotype),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)


            )

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )


            }


        }
    }
}