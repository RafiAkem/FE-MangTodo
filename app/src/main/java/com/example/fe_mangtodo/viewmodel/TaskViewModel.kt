package com.example.fe_mangtodo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe_mangtodo.data.model.TaskRequest
import com.example.fe_mangtodo.data.model.TaskResponse
import com.example.fe_mangtodo.data.network.RetrofitClient
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    var createTaskState by mutableStateOf<Result<TaskResponse>?>(null)
        private set

    fun createTask(
        title: String,
        description: String?,
        dueDate: String,
        status: String = "pending",
        userId: String
    ) {
        val request = TaskRequest(title, description, dueDate, status, userId)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.createTask(request)
                createTaskState = Result.success(response)
            } catch (e: Exception) {
                createTaskState = Result.failure(e)
            }
        }
    }
}
