package com.example.fe_mangtodo.data.local

class TaskRepository(private val dao: TaskDao) {
    suspend fun getAll() = dao.getAll()
    suspend fun insert(task: TaskEntity) = dao.insert(task)
    suspend fun update(task: TaskEntity) = dao.update(task)
    suspend fun delete(task: TaskEntity) = dao.delete(task)
}
