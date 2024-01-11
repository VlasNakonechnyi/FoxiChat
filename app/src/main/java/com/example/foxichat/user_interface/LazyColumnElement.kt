package com.example.foxichat.user_interface

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.foxichat.R
import com.example.foxichat.auth
import com.example.foxichat.dto.Room

@Composable
fun RoomInJoinRoomList(
    modifier: Modifier,
    room: Room,
    image : @Composable () -> Unit,

    ) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)

        ) {
            image()
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (room.users.contains(
                        auth.uid.toString()
                    )
                ) {
                    Text(
                        text = "You are in this room",
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
//                    Button(
//                        onClick = {
//                            viewModel.joinRoom(snackbarHostState, room.id)
//                            viewModel.getAllRooms()
//                        },
//                        shape = RoundedCornerShape(50),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
//                            contentColor = MaterialTheme.colorScheme.primary
//                        )
//
//                    ) {
//                        Text(text = "Join")
//                    }
                }
            }


        }
    }
}