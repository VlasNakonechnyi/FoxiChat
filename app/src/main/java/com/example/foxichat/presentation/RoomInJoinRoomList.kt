package com.example.foxichat.presentation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foxichat.AuthenticationWorker
import com.example.foxichat.R
import com.example.foxichat.dto.RoomDto
import com.example.foxichat.navigation.Screen
import com.example.foxichat.presentation.view_model.ChatViewModel

@Composable
fun RoomInJoinRoomList(
    roomDto: RoomDto,
    viewModel: ChatViewModel,
    nav: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(100.dp)
            .shadow(0.5.dp)
            .clickable(onClick = {
                viewModel.loadMessagesFromRoom(snackbarHostState, roomDto.id)
                nav.navigate(Screen.CHAT_SCREEN.name + "/${roomDto.id}/${roomDto.name}")
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
                    text = roomDto.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
                if (roomDto.users.contains(AuthenticationWorker.auth.uid.toString())) {
                    Text(
                        text = stringResource(id = R.string.text_already_in_room),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {

                    Button(
                        onClick = {
                            viewModel.joinRoom(snackbarHostState, roomDto.id)
                            viewModel.loadAllRooms()
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        )

                    ) {
                        Text(text = stringResource(id = R.string.text_join))
                    }

                }

            }

        }
    }


}

