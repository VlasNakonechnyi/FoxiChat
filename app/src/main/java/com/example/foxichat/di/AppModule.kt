package com.example.foxichat.di

import android.app.Application
import com.example.foxichat.api.ApiFactory
import com.example.foxichat.api.RetrofitClient
import com.example.foxichat.repository.RemoteRepository
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
    fun provideApi(): ApiFactory = RetrofitClient.getClient().create(ApiFactory::class.java)

    @Provides
    @Singleton
    fun provideChatRepository(api : ApiFactory, app: Application) = RemoteRepository(api, app)

}