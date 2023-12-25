package com.example.foxichat.user_interface

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.foxichat.auth.ChatAuth
import com.example.foxichat.dto.Message
import com.example.foxichat.dto.User
import com.example.foxichat.navigation.Screen
import com.example.foxichat.view_model.ChatViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime

class Screens(
    private val nav: NavHostController
) {
    private var viewModel = ChatViewModel()


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen() {

        Scaffold(
            modifier = Modifier.fillMaxSize(),

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
                            modifier = Modifier.fillMaxWidth(),
                            value = chatBoxValue,
                            onValueChange = { chatBoxValue = it },
                            placeholder = {
                                Text(text = "Type something")
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {

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
                state = LazyListState()

            ) {


            }


        }


    }

    @Composable
    fun MessageCard(msg: Message) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = android.R.drawable.arrow_up_float),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(end = 10.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                var isExpanded by remember {
                    mutableStateOf(false)
                }
                val surfaceColor by animateColorAsState(
                    if (isExpanded) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface, label = ""
                )

                Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                    Text(
                        text = msg.author,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 1.dp,
                        color = surfaceColor,
                        modifier = Modifier
                            .animateContentSize()
                            .padding(1.dp)
                    ) {
                        Text(
                            text = msg.body,
                            modifier = Modifier.padding(all = 4.dp),
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }


    // ***************************************** SIGNUP *******************************************************
    @Composable
    fun SignUpScreen(scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
        val title = stringResource(id = R.string.sign_up_to_foxify)

        val colors = listOf(
            Color(138, 43, 226),
            Color(139, 0, 139),
            Color(75, 0, 130),
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
                        color = Color.White,
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
        val colors = listOf(
            Color(0, 0, 0),
            Color(0, 0, 0),
        )

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
                    brush = Brush.linearGradient(colors),
                    shape = RoundedCornerShape(30.dp),
                    alpha = 0.5f
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
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White
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
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White
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
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White
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
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White
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
        val colors = listOf(
            Color(0, 0, 0),
            Color(0, 0, 0),
        )

        var emailValue by remember { mutableStateOf(TextFieldValue("")) }

        val emailPlaceholderText = stringResource(id = R.string.email_hint)

        val passwordPlaceholderText = stringResource(id = R.string.password_hint)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(colors),
                    shape = RoundedCornerShape(30.dp),
                    alpha = 0.5f
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
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White
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
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White
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
                        viewModel.signIn(email, password)

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
            Color(138, 43, 226),
            Color(139, 0, 139),
            Color(139, 0, 139),
            Color(75, 0, 130),
            Color(25, 25, 112),
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
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                //Spacer(modifier = Modifier.height(20.dp))
                SignInCol()

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen() {
        val colors = listOf(
            Color(138, 43, 226),
            Color(139, 0, 139),
            Color(139, 0, 139),
            Color(75, 0, 130),
            Color(25, 25, 112),
        )
        Scaffold(modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* do something */ },
                    shape = CircleShape,
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Outlined.Add, "Localized description")
                }
            },
            topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .background(Color(72, 61, 139))
                    .alpha(0.5f),
                title = {
                    Text(
                        "Chats",
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
                    containerColor = Color(75, 0, 130)
                ) {
                    Row (
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
                                contentDescription = "")
                        }
                    }

                }
            }
        ) {

            LazyColumn(
                modifier = Modifier.padding(it)
            ) {

            }
        }


        @Composable
        fun UserInList(user: User) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(70.dp)
                    .shadow(0.5.dp)
                    .clickable(onClick = {

                        nav.navigate(Screen.CHAT_SCREEN.name)
                    }),
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
                            .padding(end = 10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                    ) {

                    }


                }
            }
        }
    }

    companion object {

        private const val TAG = "CHAT_SCREEN"
    }

    fun timeToDbFormat(): String {

        return "${LocalDate.now()}T${LocalTime.now().toString().substringBefore(".") + "Z"}"
    }

    @Composable
    fun TestNotificationScreen() {
        Button(onClick = {
            Firebase.messaging.token.addOnCanceledListener {

            }
            Firebase.messaging.token.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result
                    Log.d(TAG, "Refreshed token: $token")
                    val retrofit = RetrofitClient.getClient()
                    val apiService = retrofit.create(ApiFactory::class.java)

                    val body = mapOf(
                        "id" to "000000000000000000000000",
                        "userId" to ChatAuth.auth.currentUser?.uid!!,
                        "deviceId" to token,
                        "timestamp" to timeToDbFormat()
                    )
                    if (token != null) {
                        apiService.postRequest(body).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                Log.d(TAG, response.body().toString())
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                //Log.d(TAG, t.message.toString())
                            }
                        })
                    } else {

                    }
                    // Log and toast

                },
            )
        }) {

        }
    }

}

