package com.example.fe_mangtodo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fe_mangtodo.data.local.entity.TaskEntity
import com.example.fe_mangtodo.data.local.entity.CategoryEntity
import com.example.fe_mangtodo.data.local.entity.UserEntity
import com.example.fe_mangtodo.data.local.dao.TaskDao
import com.example.fe_mangtodo.data.local.dao.CategoryDao
import com.example.fe_mangtodo.data.local.dao.UserDao

@Database(
    entities = [TaskEntity::class, CategoryEntity::class, UserEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
}
