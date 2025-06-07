package com.example.fe_mangtodo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val dueDate: String? = null,           // YYYY-MM-DD format
    val priority: Int = 0,                 // Misal: 0 = Low, 1 = Medium, 2 = High
    val status: String = "pending",        // Atau pakai enum
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


