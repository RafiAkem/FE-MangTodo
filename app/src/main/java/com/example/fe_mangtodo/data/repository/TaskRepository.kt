package com.example.fe_mangtodo.data.repository

import com.example.fe_mangtodo.data.local.dao.TaskDao
import com.example.fe_mangtodo.data.mapper.toEntity
import com.example.fe_mangtodo.data.mapper.toTask
import com.example.fe_mangtodo.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val dao: TaskDao) {

    fun getTasks(userId: String): Flow<List<Task>> {
        return dao.getAllTasks(userId).map { it.map { entity -> entity.toTask() } }
    }

    suspend fun insertTasks(tasks: List<Task>) {
        dao.insertTasks(tasks.map { it.toEntity() })
    }

    suspend fun deleteTask(task: Task) {
        dao.deleteById(task.id)
    }
}
