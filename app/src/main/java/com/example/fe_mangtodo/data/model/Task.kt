package com.example.fe_mangtodo.data.model

data class Task(
    val title: String,
    val description: String?,
    val dueDate: String?,
    val status: String
)

data class TaskRequest(
    val title: String,
    val description: String?,
    val dueDate: String,
    val status: String,
    val userId: String
)

data class TaskResponse(
    val success: Boolean,
    val message: String,
    val task: Task
)
