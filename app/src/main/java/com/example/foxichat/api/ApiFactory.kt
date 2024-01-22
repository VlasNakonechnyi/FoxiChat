package com.example.foxichat.api

import com.example.foxichat.dto.MessageDto
import com.example.foxichat.dto.Room
import com.example.foxichat.dto.UserDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// TODO NOTE: It is better to divide logic in API services. For instance, this ApiFactory can be
//  divided to TokenService, UserService, RoomService and MessageService. This ensures that specific
//  functions are only exposed where needed in the application, promoting a more modular and
//  organized structure.
interface ApiFactory {

    // TODO NOTE: Give the functions meaningful names to enhance clarity when they are called from
    //  various contexts. This function posts token to BE, so call it sendToken() or a similarly
    //  descriptive term to clearly indicate its purpose
    @POST("/tokens")
    fun sendNotificationToken(@Body body: Map<String, String>): Call<ResponseBody>

    @POST("/messages/send-message")
    fun sendMessage(@Body body: MessageDto) : Call<ResponseBody>

    @GET("/messages/get-messages")
    fun getMessagesFromRoom(@Query("roomid") string: String): Call<ResponseBody>

    @POST("/users/create-user")
    fun createUser(@Body body: UserDto): Call<ResponseBody>

    @GET("/rooms/get-all-rooms")
    fun getAllRooms(): Call<ResponseBody>
    @POST("/rooms/get-user-rooms")
    fun getUserRooms(@Body body: String): Call<ResponseBody>

    @POST("/rooms/create-room")
    fun createRoom(@Body body: Room): Call<ResponseBody>

    @POST("/rooms/join-room")
    fun joinRoom(@Body body: Map<String, String>): Call<ResponseBody>
}