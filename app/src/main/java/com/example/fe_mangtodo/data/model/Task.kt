package com.example.fe_mangtodo.data.model

data class Task(
    val title: String,
    val description: String?,
    val dueDate: String?,
    val status: String
)