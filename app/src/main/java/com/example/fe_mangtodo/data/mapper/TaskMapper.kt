package com.example.fe_mangtodo.data.mapper

import com.example.fe_mangtodo.data.model.Task
import com.example.fe_mangtodo.data.local.entity.TaskEntity

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate,
        dueTime = dueTime,
        status = status,
        categoryId = categoryId,
        userId = userId
    )
}

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate,
        dueTime = dueTime,
        status = status,
        categoryId = categoryId,
        userId = userId
    )
}
