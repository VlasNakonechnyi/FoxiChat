package com.example.foxichat.user_interface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun CreateRoomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (name: String) -> Unit,
    dialogTitle: String,
    icon: ImageVector,
) {
    var roomNameTextFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Column {
                TextField(
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                    value = roomNameTextFieldValue,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    onValueChange = {
                        roomNameTextFieldValue = it
                    },
                    placeholder = {
                        Text(text = "Name")
                    },

                    trailingIcon = {
                        IconButton(
                            onClick = {
                                roomNameTextFieldValue = TextFieldValue("")
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Clear,
                                contentDescription = "clear text",
                            )
                        }
                    }
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(roomNameTextFieldValue.text.trim())
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}