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
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fe_mangtodo.ui.screen.*
import com.example.fe_mangtodo.ui.theme.FEMangTodoTheme
import com.example.fe_mangtodo.viewmodel.AuthViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fe_mangtodo.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                // Mendapatkan ViewModel via Compose
                val taskViewModel: TaskViewModel = viewModel(
                )




                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        showProfile -> {
                            ProfileScreen(
                                username = authViewModel.currentUsername,
                                onNavigateBack = { showProfile = false },
                                onHomeClick = { showProfile = false },
                                onProfileClick = { /* tetap di sini */ },
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
                                onNavigateBack = { showAddTask = false },
                                onTaskAdded = { showAddTask = false },
                                viewModel = taskViewModel,
                                modifier = Modifier.padding(innerPadding)
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
                                    onSuccess = { isAuthenticated = true }
                                )
                                // Pindahkan tombol ini ke bawah LoginScreen supaya UI rapih
                                Button(
                                    onClick = { showLogin = false },
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text("Go to Register")
                                }
                            } else {
                                RegisterScreen(
                                    viewModel = authViewModel,
                                    onRegistered = { isAuthenticated = true }
                                )
                                Button(
                                    onClick = { showLogin = true },
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text("Back to Login")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
