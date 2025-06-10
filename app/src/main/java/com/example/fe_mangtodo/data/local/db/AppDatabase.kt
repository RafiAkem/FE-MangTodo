package com.example.fe_mangtodo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fe_mangtodo.data.local.entity.TaskEntity
import com.example.fe_mangtodo.data.local.dao.TaskDao


@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
