package com.example.fe_mangtodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val dueTime: String,
    val status: String,
    val categoryId: String?,
    val userId: String
)

