package com.example.fe_mangtodo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.fe_mangtodo.ui.screen.AddTaskScreen
import com.example.fe_mangtodo.ui.screen.CategoryManagementScreen
import com.example.fe_mangtodo.ui.screen.LoginScreen
import com.example.fe_mangtodo.ui.screen.ProfileScreen
import com.example.fe_mangtodo.ui.screen.RegisterScreen
import com.example.fe_mangtodo.ui.screen.TodoAppScreen
import com.example.fe_mangtodo.ui.theme.FEMangTodoTheme
import com.example.fe_mangtodo.viewmodel.AuthViewModel
import com.example.fe_mangtodo.viewmodel.CategoryViewModel
import com.example.fe_mangtodo.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FEMangTodoTheme {
                var showLogin by remember { mutableStateOf(true) }
                var isAuthenticated by remember { mutableStateOf(false) }
                var showAddTask by remember { mutableStateOf(false) }
                var showProfile by remember { mutableStateOf(false) }
                var showCategoryManagement by remember { mutableStateOf(false) }
                val authViewModel = remember { AuthViewModel() }
                val taskViewModel = remember { TaskViewModel() }
                val categoryViewModel = remember { CategoryViewModel() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        showCategoryManagement -> {
                            val userId = authViewModel.currentUserId
                            if (userId != null) {
                                CategoryManagementScreen(
                                    onNavigateBack = { showCategoryManagement = false },
                                    userId = userId,
                                    categoryViewModel = categoryViewModel
                                )
                            } else {
                                // If userId is null, go back to main screen
                                showCategoryManagement = false
                            }
                        }
                        showProfile -> {
                            ProfileScreen(
                                viewModel = authViewModel,
                                username = authViewModel.currentUsername,
                                onNavigateBack = { showProfile = false },
                                onHomeClick = { showProfile = false },
                                onProfileClick = { /* Already on profile */ },
                                onAddClick = {
                                    showProfile = false
                                    taskViewModel.resetCreateTaskState() // Reset state
                                    showAddTask = true
                                },
                                onLogout = {
                                    isAuthenticated = false
                                    showLogin = true
                                    showProfile = false
                                    authViewModel.logout()
                                }
                            )
                        }
                        showAddTask -> {
                            AddTaskScreen(
                                userId = authViewModel.currentUserId ?: "",
                                onNavigateBack = { showAddTask = false },
                                onTaskAdded = { showAddTask = false },
                                taskViewModel = taskViewModel,
                                categoryViewModel = categoryViewModel
                            )
                        }
                        isAuthenticated -> {
                            TodoAppScreen(
                                onAddTask = {
                                    taskViewModel.resetCreateTaskState()
                                    showAddTask = true
                                },
                                onProfileClick = { showProfile = true },
                                onLogout = {
                                    isAuthenticated = false
                                    showLogin = true
                                    authViewModel.logout()
                                },
                                onManageCategories = { showCategoryManagement = true },
                                username = authViewModel.currentUsername,
                                userId = authViewModel.currentUserId ?: "",
                                taskViewModel = taskViewModel,
                                authViewModel = authViewModel,
                                categoryViewModel = categoryViewModel
                            )
                        }
                        else -> {
                            if (showLogin) {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onSuccess = { isAuthenticated = true },
                                    onNavigateToRegister = { showLogin = false }
                                )
                            } else {
                                RegisterScreen(
                                    viewModel = authViewModel,
                                    onRegistered = {
                                        showLogin = true
                                    },
                                    onNavigateToLogin = { showLogin = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
