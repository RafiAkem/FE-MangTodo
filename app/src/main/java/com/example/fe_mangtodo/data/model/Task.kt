package com.example.fe_mangtodo.data.model

data class TaskResponse(
    val status: String,
    val message: String,
    val data: TaskData
)

data class TaskData(
    val task: Task
)

data class TaskRequest(
    val title: String,
    val description: String,
    val dueDate: String,
    val userId: String
)

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val status: String,
    val userId: String
)

