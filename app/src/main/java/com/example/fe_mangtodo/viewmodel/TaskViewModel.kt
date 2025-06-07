package com.example.fe_mangtodo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe_mangtodo.data.local.DatabaseProvider
import com.example.fe_mangtodo.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.fe_mangtodo.data.local.TaskEntity

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider.getDatabase(application).taskDao()
    private val repository = TaskRepository(dao)

    val tasks = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insertTask(task: TaskEntity) = viewModelScope.launch {
        repository.insert(task)
    }

    fun updateTask(task: TaskEntity) = viewModelScope.launch {
        repository.update(task)
    }

    fun deleteTask(task: TaskEntity) = viewModelScope.launch {
        repository.delete(task)
    }
}
