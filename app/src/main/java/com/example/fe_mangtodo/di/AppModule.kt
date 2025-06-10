package com.example.fe_mangtodo.di

import com.example.fe_mangtodo.data.local.dao.TaskDao
import com.example.fe_mangtodo.data.repository.TaskRepository
import com.example.fe_mangtodo.data.network.ApiService
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
    fun provideTaskRepository(dao: TaskDao, apiService: ApiService): TaskRepository {
        return TaskRepository(dao, apiService)
    }
}

