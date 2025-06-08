package com.example.fe_mangtodo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe_mangtodo.data.model.Task
import com.example.fe_mangtodo.data.model.TaskRequest
import com.example.fe_mangtodo.data.model.TaskResponse
import com.example.fe_mangtodo.data.network.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel : ViewModel() {
    var tasks by mutableStateOf<List<Task>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var createTaskState by mutableStateOf<Result<TaskResponse>?>(null)
        private set

    var deleteTaskState by mutableStateOf<Result<Unit>?>(null)
        private set

    fun loadUserTasks(userId: String, selectedDate: LocalDate? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.getUserTasks(userId)
                val allTasks = response.data

                tasks = if (selectedDate != null) {
                    allTasks.filter { task ->
                        LocalDate.parse(task.dueDate.substring(0, 10)) == selectedDate
                    }
                } else {
                    allTasks
                }
            } catch (e: Exception) {
                println("Error loading tasks: ${e.message}")
                tasks = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun createTask(
        title: String,
        description: String,
        dueDate: String,
        dueTime: String,
        categoryId: String,
        userId: String
    ) {
        val request = TaskRequest(title, description, dueDate, dueTime, categoryId, userId)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.createTask(request)
                createTaskState = Result.success(response)
                // Reload tasks after creating a new one (will reload with current selected date)
                loadUserTasks(userId)
            } catch (e: Exception) {
                createTaskState = Result.failure(e)
            }
        }
    }

    fun deleteTask(taskId: String, userId: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.api.deleteTask(taskId)
                deleteTaskState = Result.success(Unit)
                // Reload tasks after deleting one
                loadUserTasks(userId)
            } catch (e: Exception) {
                println("Error deleting task: ${e.message}")
                deleteTaskState = Result.failure(e)
            }
        }
    }

    fun resetCreateTaskState() {
        createTaskState = null
    }

    fun resetDeleteTaskState() {
        deleteTaskState = null
    }
}
