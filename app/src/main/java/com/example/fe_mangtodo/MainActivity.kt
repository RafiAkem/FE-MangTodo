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
import com.example.fe_mangtodo.ui.screen.LoginScreen
import com.example.fe_mangtodo.ui.screen.ProfileScreen
import com.example.fe_mangtodo.ui.screen.RegisterScreen
import com.example.fe_mangtodo.ui.screen.TodoAppScreen
import com.example.fe_mangtodo.ui.theme.FEMangTodoTheme
import com.example.fe_mangtodo.viewmodel.AuthViewModel

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
                val authViewModel = remember { AuthViewModel() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        showProfile -> {
                            ProfileScreen(
                                username = authViewModel.currentUsername,
                                onNavigateBack = { showProfile = false },
                                onHomeClick = { showProfile = false },
                                onProfileClick = { /* Already on profile */ },
                                onAddClick = {
                                    showProfile = false
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
                                onTaskAdded = { showAddTask = false }
                            )
                        }
                        isAuthenticated -> {
                            TodoAppScreen(
                                onAddTask = { showAddTask = true },
                                onProfileClick = { showProfile = true },
                                onLogout = {
                                    isAuthenticated = false
                                    showLogin = true
                                    authViewModel.logout()
                                },
                                modifier = Modifier.padding(innerPadding),
                                username = authViewModel.currentUsername
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
