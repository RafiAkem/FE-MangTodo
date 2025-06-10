package com.example.fe_mangtodo.data.model

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    val status: String,
    val message: String?,
    val data: TaskData
)

data class TaskData(
    val task: Task?
)

data class TaskRequest(
    val title: String,
    val description: String,
    val dueDate: String,
    val dueTime: String,
    val categoryId: String?,
    val userId: String,
    val status: String
)

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val dueTime: String,
    val status: String,
    val categoryId: String?,
    val userId: String
)

data class TasksResponse(
    val status: String,
    val message: String,
    val data: List<Task>
)

