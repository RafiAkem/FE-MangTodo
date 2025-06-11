package com.example.fe_mangtodo.di

import com.example.fe_mangtodo.data.network.ApiService
import com.example.fe_mangtodo.data.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return RetrofitClient.api
    }
}
