package com.example.fe_mangtodo.data.repository

import android.util.Log
import com.example.fe_mangtodo.data.local.dao.TaskDao
import com.example.fe_mangtodo.data.local.entity.TaskEntity
import com.example.fe_mangtodo.data.mapper.toEntity
import com.example.fe_mangtodo.data.mapper.toTask
import com.example.fe_mangtodo.data.model.Task
import com.example.fe_mangtodo.data.model.TaskRequest
import com.example.fe_mangtodo.data.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val dao: TaskDao,
    private val apiService: ApiService,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "TaskRepository"
    private val TEMP_TASK_PREFIX = "temp_"

    fun getTasks(userId: String): Flow<List<Task>> {
        Log.d(TAG, "Getting tasks from local database for user: $userId")
        // First return local data immediately
        val localFlow = dao.getAllTasks(userId).map { entities ->
            Log.d(TAG, "Retrieved ${entities.size} tasks from local database")
            entities.map { it.toTask() }
        }

        // Then attempt to sync in the background
        coroutineScope.launch {
            try {
                syncTasks(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Background sync failed: ${e.message}")
                // Don't throw the error since we're in background
            }
        }

        return localFlow
    }

    suspend fun syncTasks(userId: String) {
        try {
            Log.d(TAG, "Syncing tasks from remote for user: $userId")
            val response = apiService.getUserTasks(userId)
            val tasks = response.data
            Log.d(TAG, "Retrieved ${tasks.size} tasks from remote")
            
            // Log task details for debugging
            tasks.forEach { task ->
                Log.d(TAG, "Task details - ID: ${task.id}, Title: ${task.title}, CategoryId: ${task.categoryId}")
            }
            
            val entities = tasks.map { it.toEntity() }
            Log.d(TAG, "Converting ${entities.size} tasks to entities")
            dao.insertTasks(entities)
            Log.d(TAG, "Saved ${entities.size} tasks to local database")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing tasks: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            // Don't throw the error, just log it and continue with local data
        }
    }

    suspend fun createTask(request: TaskRequest): Task {
        try {
            Log.d(TAG, "Creating task with request: $request")
            
            // Create a temporary task with a unique ID
            val tempTask = Task(
                id = "${TEMP_TASK_PREFIX}${System.currentTimeMillis()}",
                title = request.title,
                description = request.description,
                dueDate = request.dueDate,
                dueTime = request.dueTime,
                status = request.status,
                categoryId = request.categoryId,
                userId = request.userId
            )
            
            // Save to local database first
            Log.d(TAG, "Saving task to local database")
            dao.insertTask(tempTask.toEntity())
            
            // Try to sync with remote
            try {
                Log.d(TAG, "Attempting to sync task with remote")
                val response = apiService.createTask(request)
                Log.d(TAG, "API Response: $response")

                if (response.status == "success" && response.data.task != null) {
                    Log.d(TAG, "Task created successfully on backend")
                    // Update local task with the real ID
                    val realTask = response.data.task
                    dao.deleteById(tempTask.id) // Remove temporary task
                    dao.insertTask(realTask.toEntity()) // Insert real task
                    return realTask
                } else {
                    Log.e(TAG, "Failed to create task: Backend did not return success status")
                    // Keep the temporary task in local database
                    return tempTask
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing task with remote: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                // Keep the temporary task in local database
                return tempTask
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating task: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }

    suspend fun updateTask(taskId: String, request: TaskRequest): Task {
        try {
            Log.d(TAG, "Updating task: $taskId with request: $request")
            
            // Create updated task object
            val updatedTask = Task(
                id = taskId,
                title = request.title,
                description = request.description,
                dueDate = request.dueDate,
                dueTime = request.dueTime,
                status = request.status,
                categoryId = request.categoryId,
                userId = request.userId
            )
            
            // Update in local DB first
            val entity = updatedTask.toEntity()
            dao.deleteById(taskId) // Delete old version first
            dao.insertTask(entity)
            Log.d(TAG, "Task updated in local database")
            
            // Try to sync with remote
            try {
                val response = apiService.updateTask(taskId, request)
                Log.d(TAG, "API Response for update: $response")

                if (response.status == "success" && response.data.task != null) {
                    Log.d(TAG, "Task updated successfully on backend")
                    // Update local task with the response data
                    val realTask = response.data.task
                    dao.deleteById(taskId)
                    dao.insertTask(realTask.toEntity())
                    return realTask
                } else {
                    Log.e(TAG, "Failed to update task: Backend did not return success status")
                    // Keep the local update
                    return updatedTask
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing task update with remote: ${e.message}")
                // Keep the local update
                return updatedTask
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }

    suspend fun deleteTask(taskId: String, userId: String) {
        try {
            Log.d(TAG, "Deleting task: $taskId")
            
            // Delete from local database first
            dao.deleteById(taskId)
            Log.d(TAG, "Task removed from local database")
            
            // Try to sync with remote
            try {
                apiService.deleteTask(taskId, userId)
                Log.d(TAG, "Task deleted from remote")
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing task deletion with remote: ${e.message}")
                // Task is already deleted locally, so we don't need to do anything
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }
}
