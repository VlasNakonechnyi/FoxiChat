package com.example.foxichat.user_interface

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.foxichat.R
import com.example.foxichat.api.ApiFactory
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room
import com.example.foxichat.navigation.Screen
import com.example.foxichat.view_model.ChatViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class Screens(
    private val nav: NavHostController,
    private val viewModel: ChatViewModel,
    private val snackbarHostState: SnackbarHostState,
) {



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(chatId: String?) {
        val messages by viewModel.getMessages().observeAsState()
        val state = rememberLazyListState(initialFirstVisibleItemIndex = messages?.size?.minus(1) ?: 0)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                        titleContentColor = MaterialTheme.colorScheme.secondary,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary,
                    ),

                    title = {
                        Text(
                            "Room",
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            nav.navigate(Screen.HOME.name)
                        }) {
                            Icon(
                                Icons.Outlined.KeyboardArrowLeft,
                                contentDescription = ""
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                Icons.Outlined.MoreVert,
                                contentDescription = ""
                            )
                        }
                    }

                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }
                    Row {
                        TextField(
                            shape = RoundedCornerShape(50.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            value = chatBoxValue,
                            onValueChange = { chatBoxValue = it },
                            placeholder = {
                                Text(text = "Type something")
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val body = chatBoxValue.text.trim()
                                        if (body.isNotBlank()) {
                                            chatId?.let { viewModel.sendMessage(body, it) }
                                        }
                                    },
                                ) {
                                    Icon(
                                        Icons.Default.Send,
                                        contentDescription = ""
                                    )
                                }
                            }
                        )

                    }
                }
            }
        ) { innerPadding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = state

            ) {
                if (messages != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val index = if (messages!!.size-1 >=0) messages!!.size-1 else 0
                        state.scrollToItem(index = index)
                    }

                    items(messages!!) {
                        MessageCard(msg = it)
                    }
                }
            }


        }


    }
    @Composable
    fun MyMessage(msg: MessageDto) {

        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text =  msg.authorName,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.large,
                shadowElevation = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
            }
            Text(
                text = msg.timeStamp,
                modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.titleSmall,
                fontSize = 12.sp
            )

        }
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(id = R.drawable.logotype),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .padding(end = 10.dp)
        )
    }
    @Composable
    fun NotMyMessage(msg: MessageDto) {
        Image(
            painter = painterResource(id = R.drawable.logotype),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .padding(end = 10.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text =  msg.authorName,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.large,
                shadowElevation = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
            }
            Text(
                text = msg.timeStamp,
                modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.titleSmall,
                fontSize = 12.sp
            )
        }
        
        
    }


    @Composable
    fun MessageCard(msg: MessageDto) {
        val isFromMe = viewModel.isMessageFromMe(msg)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start

            ) {
                if (isFromMe) {
                    MyMessage(msg = msg)
                } else {
                    NotMyMessage(msg = msg)
                }
            }
        }
    }


    // ***************************************** SIGNUP *******************************************************
    @Composable
    fun SignUpScreen(scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
        val title = stringResource(id = R.string.sign_up_to_foxify)

        val colors = listOf(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.surface,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()

                .background(brush = Brush.linearGradient(colors), alpha = 0.5f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(150.dp),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo"
                    )
                    //Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                //Spacer(modifier = Modifier.height(20.dp))
                SignUpCol(scope, snackbarHostState)

            }
        }

    }

    @Composable
    fun SignUpCol(scope: CoroutineScope, snackbarHostState: SnackbarHostState) {

        var emailValue by remember { mutableStateOf(TextFieldValue("")) }
        var isValidEmail by remember { mutableStateOf(true) }

        var usernameValue by remember { mutableStateOf(TextFieldValue("")) }

        var phoneNumberValue by remember { mutableStateOf(TextFieldValue("")) }
        var isValidPhoneNumber by remember { mutableStateOf(true) }

        val emailPlaceholderText = stringResource(id = R.string.email_hint)
        val phoneNumberPlaceholderText = stringResource(id = R.string.phone_number_hint)
        val passwordPlaceholderText = stringResource(id = R.string.password_hint)
        val usernamePlaceHolderText = stringResource(id = R.string.username_hint)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(5),

                    )
                .verticalScroll(enabled = true, state = ScrollState(0))

        ) {


            TextField(
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                value = emailValue,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                onValueChange = {
                    emailValue = it
                    isValidEmail = viewModel.validateEmailField(emailValue.text)
                },
                placeholder = {
                    Text(text = emailPlaceholderText)
                },
                supportingText = {
                    if (!isValidEmail) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Email must be valid",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            emailValue = TextFieldValue("")
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Clear,
                            contentDescription = "clear text",
                        )
                    }
                }
            )



            TextField(
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                value = usernameValue,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                onValueChange = {
                    usernameValue = it
                },
                placeholder = {
                    Text(text = usernamePlaceHolderText)
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            usernameValue = TextFieldValue("")
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Clear,
                            contentDescription = "clear text",
                        )
                    }
                }
            )

            TextField(
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                value = phoneNumberValue,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                onValueChange = {
                    phoneNumberValue = it
                    isValidPhoneNumber = viewModel.validatePhoneNumberField(phoneNumberValue.text)
                },
                placeholder = {
                    Text(text = phoneNumberPlaceholderText)
                },
                supportingText = {
                    if (!isValidPhoneNumber) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Phone number must be valid",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            phoneNumberValue = TextFieldValue("")
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Clear,
                            contentDescription = "clear text",
                        )
                    }
                }
            )

            var passwordValue by remember { mutableStateOf(TextFieldValue("")) }
            var isPasswordVisible by remember { mutableStateOf(false) }
            var isPasswordValid by remember { mutableStateOf(true) }

            TextField(
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                value = passwordValue,
                onValueChange = {
                    isPasswordValid = viewModel.validatePasswordField(passwordValue.text)
                    // if (!isPasswordValid) return@TextField
                    passwordValue = it
                    isPasswordValid = true

                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                supportingText = {
                    if (!isPasswordValid) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Password must not contain spaces",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                placeholder = {
                    Text(text = passwordPlaceholderText)
                },
                trailingIcon = {
                    if (isPasswordValid) {
                        IconButton(
                            onClick = {
                                isPasswordVisible = !isPasswordVisible
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.eye),
                                contentDescription = "clear text",
                            )
                        }
                    } else {
                        Icon(Icons.Filled.Info, "error", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
            Button(
                onClick = {

                    isValidEmail = viewModel.validateEmailField(emailValue.text)
                    isPasswordValid = viewModel.validatePasswordField(passwordValue.text)
                    isValidPhoneNumber = viewModel.validatePhoneNumberField(phoneNumberValue.text)

                    if (isValidEmail && isPasswordValid && isValidPhoneNumber) {
                        val email = emailValue.text.trim()
                        val password = passwordValue.text
                        val userName = usernameValue.text.trim()
                        val phoneNumber = phoneNumberValue.text.trim()
                        viewModel.addNewUser(
                            nav = nav,
                            scope = scope,
                            hostState = snackbarHostState,
                            email = email,
                            password = password,
                            username = userName,
                            phone = phoneNumber
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
            ) {
                Text(text = "Sign Up")
            }
            TextButton(
                onClick = {
                    nav.navigate(Screen.SIGNIN.name)
                },
                modifier = Modifier
                    .padding(top = 10.dp),

                ) {
                Text("I have an account")
            }
        }


    }

    @Composable
    fun SignInCol() {

        var emailValue by remember { mutableStateOf(TextFieldValue("")) }

        val emailPlaceholderText = stringResource(id = R.string.email_hint)

        val passwordPlaceholderText = stringResource(id = R.string.password_hint)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(5)
                )
                .verticalScroll(enabled = true, state = ScrollState(0))

        ) {


            TextField(
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                value = emailValue,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                onValueChange = {
                    emailValue = it
                },
                placeholder = {
                    Text(text = emailPlaceholderText)
                },

                trailingIcon = {
                    IconButton(
                        onClick = {
                            emailValue = TextFieldValue("")
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Clear,
                            contentDescription = "clear text",
                        )
                    }
                }
            )


            var passwordValue by remember { mutableStateOf(TextFieldValue("")) }
            var isPasswordVisible by remember { mutableStateOf(false) }


            TextField(
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                value = passwordValue,
                onValueChange = {
                    passwordValue = it
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),

                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                placeholder = {
                    Text(text = passwordPlaceholderText)
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            isPasswordVisible = !isPasswordVisible
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.eye),
                            contentDescription = "clear text",
                        )
                    }

                }
            )

//            Text(
//                modifier = Modifier.padding(start = 20.dp),
//                text = if (isSignInSuccessful) "" else "Wrong email or password",
//                color = MaterialTheme.colorScheme.error
//            )
            Button(
                onClick = {

                    val email = emailValue.text.trim()
                    val password = passwordValue.text
                    if (email.isNotBlank() && password.isNotBlank()) {
                        viewModel.signIn(nav, email, password)


                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
            ) {
                Text(text = "Sign In")
            }
            TextButton(
                onClick = {
                    nav.navigate(Screen.SIGNUP.name)
                },
                modifier = Modifier
                    .padding(top = 10.dp),

                ) {
                Text("I don`t have an account")
            }
        }


    }

    @Composable
    fun SignInScreen() {
        val title = stringResource(id = R.string.welcome_text)

        val colors = listOf(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.surface,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()

                .background(brush = Brush.linearGradient(colors), alpha = 0.5f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(150.dp),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo"
                    )
                    //Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                //Spacer(modifier = Modifier.height(20.dp))
                SignInCol()

            }
        }
    }

    //******************************************** HOME SCREEN *********************************************

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    fun HomeScreen() {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }
        var openAlertDialog by remember { mutableStateOf(false) }
        val refreshScope = rememberCoroutineScope()
        var refreshing by remember { mutableStateOf(false) }
        val userRoomsList by viewModel.userRoomList.observeAsState()
        val allRoomsList by viewModel.roomsList.observeAsState()


        fun refresh() = refreshScope.launch {
            refreshing = true
            val res = async { viewModel.loadUserRooms() }
            res.await()
            refreshing = false
        }
        val state = rememberPullRefreshState(refreshing, ::refresh)

        Scaffold(modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.getAllRooms()
                        showBottomSheet = true

                              },
                    shape = CircleShape,
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Outlined.Add, "Localized description")
                }

            },
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                        titleContentColor = MaterialTheme.colorScheme.secondary,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary,
                    ),

                    title = {
                        Text(
                            "Chats",
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.signOut()
                            nav.navigate(Screen.SIGNIN.name)

                        }) {
                            Icon(
                                Icons.Outlined.KeyboardArrowLeft,
                                contentDescription = ""
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                Icons.Outlined.AccountCircle,
                                contentDescription = ""
                            )
                        }
                    }

                )
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .height(50.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = "",

                                )
                        }
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_icons),
                                contentDescription = "clear text",
                            )
                        }
                        IconButton(onClick = { /* do something */ }) {
                            Image(
                                painter = painterResource(id = R.drawable.spoti_logo),
                                contentDescription = ""
                            )
                        }
                    }

                }
            }
        ) { it ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)


            ) {
                LazyColumn {
                    if (userRoomsList!= null) {
                        items(userRoomsList!!) {
                            RoomInUserRoomsList(room = it)
                        }
                    }

                }
                PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
            }

            when {
                openAlertDialog -> {
                    CreateRoomAlertDialog(
                        onDismissRequest = {
                            openAlertDialog = false
                            showBottomSheet = false
                                           },
                        onConfirmation = {name ->

                            viewModel.createNewRoom(nav, snackbarHostState, scope, name = name)
                            openAlertDialog = false
                            showBottomSheet = false
                            viewModel.loadUserRooms()

                        },
                        dialogTitle = "Create new room",
                        icon = Icons.Outlined.Add
                    )
                }
            }


            if (showBottomSheet) {
                ModalBottomSheet(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    // Sheet content
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TextButton(onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            }
                            ) {
                                Text(text = "Cancel")
                            }
                            Button(onClick = {
                                openAlertDialog = true
                               
                            }) {
                                Text(text = "Create new room")
                            }

                        }

                        LazyColumn(
                            modifier = Modifier.padding(it)
                        ) {
                            if (allRoomsList != null) {
                                items(items = allRoomsList!!) {
                                    RoomInJoinRoomList(room = it)
                                }
                            }

                        }
                    }

                }
            }
        }

    }
    @Composable
    fun RoomInUserRoomsList(room: Room) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(100.dp)
                .shadow(0.5.dp)
                .clickable(onClick = {
                    ChatViewModel.currentChatId = room.id
                    viewModel.loadMessagesFromRoom(snackbarHostState, room.id)
                    nav.navigate(Screen.CHAT_SCREEN.name + "/${room.id}")
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
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
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
                    Box {
                        Text(
                            text = "Today, 14:00",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                }


            }
        }
    }


    @Composable
    fun RoomInJoinRoomList(room: Room) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(70.dp)
                .shadow(0.5.dp),
//                .clickable(onClick = {
//
//                    nav.navigate(Screen.CHAT_SCREEN.name)
//                }),
            contentAlignment = Alignment.CenterStart

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)

            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)

                )
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
                    if (room.users.contains(viewModel.auth.uid.toString())) {
                        Text(
                            text = "You are in this room",
                            color = MaterialTheme.colorScheme.primary
                            )
                    } else {
                        Button(
                            onClick = {
                                viewModel.joinRoom(snackbarHostState,room.id)
                                viewModel.getAllRooms()
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary
                            )

                        ) {
                            Text(text = "Join")
                        }
                    }
                }


            }
        }
    }
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
    companion object {
        private const val TAG = "CHAT_SCREEN"
    }
    @Composable
    fun TestNotificationScreen() {
        Button(onClick = {

        }) {

        }
    }

}

