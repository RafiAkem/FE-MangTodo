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

    fun getTasks(userId: String): Flow<List<Task>> {
        Log.d(TAG, "Getting tasks from local database for user: $userId")
        return dao.getAllTasks(userId).map { entities ->
            Log.d(TAG, "Retrieved ${entities.size} tasks from local database")
            entities.map { it.toTask() }
        }
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
            throw e
        }
    }

    suspend fun createTask(request: TaskRequest): Task {
        try {
            Log.d(TAG, "Creating task with request: $request")
            val response = apiService.createTask(request)
            Log.d(TAG, "API Response: $response")

            var taskToReturn = response.data.task

            if (taskToReturn == null && response.status == "success") {
                Log.d(TAG, "Task created on backend but not returned, fetching tasks to get the created task")
                delay(2000) // Initial delay

                var retryCount = 0
                while (retryCount < 5 && taskToReturn == null) {
                    val tasksResponse = apiService.getUserTasks(request.userId)
                    Log.d(TAG, "Retrieved ${tasksResponse.data.size} tasks in retry attempt $retryCount")
                    
                    tasksResponse.data.forEach { task ->
                        Log.d(TAG, "Checking task - ID: ${task.id}, Title: ${task.title}, Status: ${task.status}")
                    }
                    
                    taskToReturn = tasksResponse.data.find {
                        it.title == request.title &&
                        it.status == request.status &&
                        it.categoryId == request.categoryId
                    }
                    
                    if (taskToReturn == null) {
                        retryCount++
                        if (retryCount < 5) {
                            Log.d(TAG, "Task not found, retrying... (attempt $retryCount)")
                            delay(2000)
                        }
                    }
                }

                if (taskToReturn == null) {
                    Log.d(TAG, "Task not found immediately after creation, but backend reported success. Creating temporary task and triggering sync.")
                    // Create a temporary task with a placeholder ID
                    taskToReturn = Task(
                        id = "temp_${System.currentTimeMillis()}",
                        title = request.title,
                        description = request.description,
                        dueDate = request.dueDate,
                        dueTime = request.dueTime,
                        status = request.status,
                        categoryId = request.categoryId,
                        userId = request.userId
                    )
                    
                    // Save the temporary task to local database
                    val entity = taskToReturn.toEntity()
                    dao.insertTask(entity)
                    
                    // Trigger a sync in the background
                    coroutineScope.launch {
                        try {
                            syncTasks(request.userId)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error during background sync: ${e.message}")
                        }
                    }
                }
            }

            if (taskToReturn != null) {
                Log.d(TAG, "Task successfully retrieved or created: $taskToReturn")
                val entity = taskToReturn.toEntity()
                Log.d(TAG, "Converted to entity: $entity")
                dao.insertTask(entity)
                Log.d(TAG, "Task saved to local database")
                return taskToReturn
            } else {
                Log.e(TAG, "Failed to create task: Backend did not return success status")
                throw Exception("Failed to create task: Backend did not return success status.")
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
            
            val response = apiService.updateTask(taskId, request)
            Log.d(TAG, "API Response for update: $response")
            val task = response.data.task

            var taskToReturn = task

            if (taskToReturn == null && response.status == "success") {
                Log.d(TAG, "Task updated on backend but not returned, fetching updated task")
                delay(2000) // Initial delay
                
                var retryCount = 0
                while (retryCount < 5 && taskToReturn == null) {
                    val tasksResponse = apiService.getUserTasks(request.userId)
                    Log.d(TAG, "Retrieved ${tasksResponse.data.size} tasks in retry attempt $retryCount")
                    
                    tasksResponse.data.forEach { task ->
                        Log.d(TAG, "Checking task - ID: ${task.id}, Title: ${task.title}, Status: ${task.status}")
                    }
                    
                    taskToReturn = tasksResponse.data.find { 
                        it.id == taskId && 
                        it.title == request.title && 
                        it.status == request.status &&
                        it.categoryId == request.categoryId
                    }
                    
                    if (taskToReturn == null) {
                        retryCount++
                        if (retryCount < 5) {
                            Log.d(TAG, "Updated task not found, retrying... (attempt $retryCount)")
                            delay(2000)
                        }
                    }
                }

                if (taskToReturn == null) {
                    Log.d(TAG, "Task not found immediately after update, but backend reported success. Creating temporary task and triggering sync.")
                    // Create a temporary task with the same ID
                    taskToReturn = Task(
                        id = taskId,
                        title = request.title,
                        description = request.description,
                        dueDate = request.dueDate,
                        dueTime = request.dueTime,
                        status = request.status,
                        categoryId = request.categoryId,
                        userId = request.userId
                    )
                    
                    // Update the task in local database
                    val entity = taskToReturn.toEntity()
                    dao.deleteById(taskId) // Delete old version first
                    dao.insertTask(entity)
                    
                    // Trigger a sync in the background
                    coroutineScope.launch {
                        try {
                            syncTasks(request.userId)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error during background sync: ${e.message}")
                        }
                    }
                }
            }

            if (taskToReturn != null) {
                Log.d(TAG, "Task successfully retrieved or updated: $taskToReturn")
                // Delete the old task first to prevent duplicates
                dao.deleteById(taskId)
                val entity = taskToReturn.toEntity()
                Log.d(TAG, "Converted updated task to entity: $entity")
                dao.insertTask(entity)
                Log.d(TAG, "Task saved to local database")
                return taskToReturn
            } else {
                Log.e(TAG, "Failed to update task: Backend did not return success status")
                throw Exception("Failed to update task: Backend did not return success status.")
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
            
            apiService.deleteTask(taskId, userId)
            Log.d(TAG, "Task deleted from remote, removing from local database")
            dao.deleteById(taskId)
            Log.d(TAG, "Task removed from local database")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }
}
