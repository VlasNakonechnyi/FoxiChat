package com.example.foxichat.di

import android.app.Application
import com.example.foxichat.api.MessagingService
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.api.RoomService
import com.example.foxichat.api.TokenService
import com.example.foxichat.api.UserService
import com.example.foxichat.repository.RemoteRepository
import com.example.foxichat.repository.SpotifyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMessagingService(): MessagingService = RetrofitClient.getClient().create(MessagingService::class.java)
    @Provides
    @Singleton
    fun provideRoomService(): RoomService = RetrofitClient.getClient().create(RoomService::class.java)
    @Provides
    @Singleton
    fun provideTokenService(): TokenService = RetrofitClient.getClient().create(TokenService::class.java)
    @Provides
    @Singleton
    fun provideUserService(): UserService = RetrofitClient.getClient().create(UserService::class.java)

    @Provides
    @Singleton
    fun provideChatRepository(
        userService: UserService,
        roomService: RoomService,
        tokenService: TokenService,
        messagingService: MessagingService,
        app: Application) = RemoteRepository(userService, tokenService, roomService, messagingService, app)

    @Provides
    @Singleton
    fun provideSpotifyRepository(app: Application) = SpotifyRepository(app)

}