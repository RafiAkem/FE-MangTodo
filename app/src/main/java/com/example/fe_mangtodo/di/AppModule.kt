package com.example.fe_mangtodo.di

import com.example.fe_mangtodo.data.local.dao.TaskDao
import com.example.fe_mangtodo.data.local.dao.CategoryDao
import com.example.fe_mangtodo.data.repository.TaskRepository
import com.example.fe_mangtodo.data.repository.CategoryRepository
import com.example.fe_mangtodo.data.network.ApiService
import com.example.fe_mangtodo.data.local.dao.UserDao
import com.example.fe_mangtodo.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        dao: TaskDao, 
        apiService: ApiService,
        coroutineScope: CoroutineScope
    ): TaskRepository {
        return TaskRepository(dao, apiService, coroutineScope)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        dao: CategoryDao,
        apiService: ApiService,
        coroutineScope: CoroutineScope
    ): CategoryRepository {
        return CategoryRepository(dao, apiService, coroutineScope)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        dao: UserDao,
        apiService: ApiService
    ): AuthRepository {
        return AuthRepository(dao, apiService)
    }
}

