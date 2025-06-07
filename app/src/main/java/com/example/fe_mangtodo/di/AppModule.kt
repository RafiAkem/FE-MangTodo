package com.example.fe_mangtodo.di

import android.content.Context
import androidx.room.Room
import com.example.fe_mangtodo.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mangtodo_db"
        ).build()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}

