package com.example.fe_mangtodo.data.repository

import com.example.fe_mangtodo.data.local.TaskDao
import com.example.fe_mangtodo.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    fun getAll(): Flow<List<TaskEntity>> = dao.getAllTasks()

    suspend fun insert(task: TaskEntity) = dao.insertTask(task)

    suspend fun update(task: TaskEntity) = dao.updateTask(task)

    suspend fun delete(task: TaskEntity) = dao.deleteTask(task)
}
