package com.example.fe_mangtodo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fe_mangtodo.data.local.entity.TaskEntity
import com.example.fe_mangtodo.data.local.entity.CategoryEntity
import com.example.fe_mangtodo.data.local.dao.TaskDao
import com.example.fe_mangtodo.data.local.dao.CategoryDao

@Database(entities = [TaskEntity::class, CategoryEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
}
