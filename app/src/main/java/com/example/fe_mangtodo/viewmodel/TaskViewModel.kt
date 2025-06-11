package com.example.fe_mangtodo.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe_mangtodo.data.model.Task
import com.example.fe_mangtodo.data.model.TaskRequest
import com.example.fe_mangtodo.data.model.TaskResponse
import com.example.fe_mangtodo.data.model.TaskData
import com.example.fe_mangtodo.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
): ViewModel() {
    private val TAG = "TaskViewModel"

    var tasks by mutableStateOf<List<Task>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var createTaskState by mutableStateOf<Result<TaskResponse>?>(null)
        private set

    var deleteTaskState by mutableStateOf<Result<Unit>?>(null)
        private set

    var updateTaskState by mutableStateOf<Result<TaskResponse>?>(null)
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserTasks(userId: String, selectedDate: LocalDate? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d(TAG, "Loading user tasks for user: $userId and date: $selectedDate")
                // First sync with remote
                repository.syncTasks(userId)
                
                // Then observe local data
                repository.getTasks(userId).collect { allTasks ->
                    tasks = if (selectedDate != null) {
                        allTasks.filter { task ->
                            LocalDate.parse(task.dueDate.substring(0, 10)) == selectedDate
                        }
                    } else {
                        allTasks
                    }
                    Log.d(TAG, "Tasks loaded: ${tasks.size}")
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tasks: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                tasks = emptyList()
                isLoading = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTask(
        title: String,
        description: String,
        dueDate: String,
        dueTime: String,
        categoryId: String,
        userId: String,
        status: String = "in_progress"
    ) {
        val request = TaskRequest(title, description, dueDate, dueTime, categoryId, userId, status)
        viewModelScope.launch {
            isLoading = true // Set loading to true when creating task
            try {
                Log.d(TAG, "Attempting to create task with request: $request")
                val task = repository.createTask(request)
                createTaskState = Result.success(TaskResponse("success", "Task created", TaskData(task)))
                Log.d(TAG, "Task created successfully. Reloading tasks.")
                // Reload tasks after successful creation
                loadUserTasks(userId, LocalDate.parse(dueDate.substring(0, 10)))
            } catch (e: Exception) {
                Log.e(TAG, "Error creating task: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                createTaskState = Result.failure(e)
            } finally {
                isLoading = false // Set loading to false after task creation attempt
                Log.d(TAG, "isLoading set to false after createTask")
            }
        }
    }

    fun updateTask(
        taskId: String,
        title: String,
        description: String,
        dueDate: String,
        dueTime: String,
        categoryId: String,
        userId: String,
        status: String = "in_progress"
    ) {
        val request = TaskRequest(title, description, dueDate, dueTime, categoryId, userId, status)
        viewModelScope.launch {
            isLoading = true // Set loading to true when updating task
            try {
                val task = repository.updateTask(taskId, request)
                updateTaskState = Result.success(TaskResponse("success", "Task updated", TaskData(task)))
                Log.d(TAG, "Task updated successfully. Reloading tasks.")
                loadUserTasks(userId, LocalDate.parse(dueDate.substring(0, 10)))
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                updateTaskState = Result.failure(e)
            } finally {
                isLoading = false // Set loading to false after task update attempt
                Log.d(TAG, "isLoading set to false after updateTask")
            }
        }
    }

    fun deleteTask(taskId: String, userId: String) {
        viewModelScope.launch {
            isLoading = true // Set loading to true when deleting task
            try {
                repository.deleteTask(taskId, userId)
                deleteTaskState = Result.success(Unit)
                Log.d(TAG, "Task deleted successfully. Reloading tasks.")
                loadUserTasks(userId) // Reload all tasks after deletion
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting task: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                deleteTaskState = Result.failure(e)
            } finally {
                isLoading = false // Set loading to false after task deletion attempt
                Log.d(TAG, "isLoading set to false after deleteTask")
            }
        }
    }

    fun updateTaskStatus(task: Task, newStatus: String, userId: String) {
        val request = TaskRequest(
            title = task.title,
            description = task.description,
            dueDate = task.dueDate,
            dueTime = task.dueTime,
            categoryId = task.categoryId,
            userId = userId,
            status = newStatus
        )
        viewModelScope.launch {
            isLoading = true
            try {
                val updatedTask = repository.updateTask(task.id, request)
                updateTaskState = Result.success(TaskResponse("success", "Task status updated", TaskData(updatedTask)))
                Log.d(TAG, "Task status updated successfully. Reloading tasks.")
                loadUserTasks(userId, LocalDate.parse(task.dueDate.substring(0, 10)))
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task status: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                updateTaskState = Result.failure(e)
            } finally {
                isLoading = false
                Log.d(TAG, "isLoading set to false after updateTaskStatus")
            }
        }
    }

    fun resetCreateTaskState() {
        createTaskState = null
    }

    fun resetDeleteTaskState() {
        deleteTaskState = null
    }

    fun resetUpdateTaskState() {
        updateTaskState = null
    }
}
