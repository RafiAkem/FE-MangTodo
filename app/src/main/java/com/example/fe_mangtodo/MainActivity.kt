package com.example.fe_mangtodo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.fe_mangtodo.ui.screen.*
import com.example.fe_mangtodo.ui.theme.FEMangTodoTheme
import com.example.fe_mangtodo.viewmodel.AuthViewModel
import com.example.fe_mangtodo.viewmodel.CategoryViewModel
import com.example.fe_mangtodo.viewmodel.TaskViewModel

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FEMangTodoTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showLogin by remember { mutableStateOf(true) }
                var isAuthenticated by remember { mutableStateOf(false) }
                var showAddTask by remember { mutableStateOf(false) }
                var showProfile by remember { mutableStateOf(false) }
                var showCategoryManagement by remember { mutableStateOf(false) }

                var isNavigatingForward by remember { mutableStateOf(true) }

                val authViewModel = remember { AuthViewModel() }
                val taskViewModel = remember { TaskViewModel() }
                val categoryViewModel = remember { CategoryViewModel() }

                if (showSplash) {
                    SplashScreen(onNavigateToMain = { showSplash = false })
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val targetScreen = when {
                            showCategoryManagement -> "category"
                            showProfile -> "profile"
                            showAddTask -> "addtask"
                            isAuthenticated -> "home"
                            showLogin -> "login"
                            else -> "register"
                        }

                        AnimatedContent(
                            targetState = targetScreen,
                            transitionSpec = {
                                when (targetState) {
                                    "category" -> slideInHorizontally(
                                        animationSpec = tween(300)
                                    ) { fullWidth -> fullWidth } + fadeIn() with
                                            slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth } + fadeOut()

                                    "home" -> {
                                        if (isNavigatingForward) {
                                            slideInHorizontally(
                                                animationSpec = tween(300)
                                            ) { fullWidth -> fullWidth } + fadeIn() with
                                                    slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth } + fadeOut()
                                        } else {
                                            slideInHorizontally(
                                                animationSpec = tween(300)
                                            ) { fullWidth -> -fullWidth } + fadeIn() with
                                                    slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth } + fadeOut()
                                        }
                                    }

                                    "addtask" -> fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))

                                    else -> fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                                }.using(SizeTransform(clip = false))
                            }
                        ) { target ->
                            when (target) {
                                "category" -> {
                                    val userId = authViewModel.currentUserId
                                    if (userId != null) {
                                        CategoryManagementScreen(
                                            onNavigateBack = {
                                                isNavigatingForward = false
                                                showCategoryManagement = false
                                            },
                                            userId = userId,
                                            categoryViewModel = categoryViewModel
                                        )
                                    } else {
                                        showCategoryManagement = false
                                    }
                                }

                                "profile" -> {
                                    ProfileScreen(
                                        viewModel = authViewModel,
                                        username = authViewModel.currentUsername,
                                        onNavigateBack = {
                                            isNavigatingForward = false
                                            showProfile = false
                                        },
                                        onHomeClick = {
                                            isNavigatingForward = false
                                            showProfile = false
                                        },
                                        onProfileClick = { /* Already on profile */ },
                                        onAddClick = {
                                            isNavigatingForward = true
                                            showProfile = false
                                            taskViewModel.resetCreateTaskState()
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

                                "addtask" -> {
                                    AddTaskScreen(
                                        userId = authViewModel.currentUserId ?: "",
                                        onNavigateBack = {
                                            isNavigatingForward = false
                                            showAddTask = false
                                        },
                                        onTaskAdded = {
                                            isNavigatingForward = false
                                            showAddTask = false
                                        },
                                        taskViewModel = taskViewModel,
                                        categoryViewModel = categoryViewModel
                                    )
                                }

                                "home" -> {
                                    TodoAppScreen(
                                        onAddTask = {
                                            isNavigatingForward = true
                                            taskViewModel.resetCreateTaskState()
                                            showAddTask = true
                                        },
                                        onProfileClick = {
                                            isNavigatingForward = true
                                            showProfile = true
                                        },
                                        onLogout = {
                                            isAuthenticated = false
                                            showLogin = true
                                            authViewModel.logout()
                                        },
                                        onManageCategories = {
                                            isNavigatingForward = true
                                            showCategoryManagement = true
                                        },
                                        username = authViewModel.currentUsername,
                                        userId = authViewModel.currentUserId ?: "",
                                        taskViewModel = taskViewModel,
                                        authViewModel = authViewModel,
                                        categoryViewModel = categoryViewModel
                                    )
                                }

                                "login" -> {
                                    LoginScreen(
                                        viewModel = authViewModel,
                                        onSuccess = {
                                            isAuthenticated = true
                                        },
                                        onNavigateToRegister = {
                                            showLogin = false
                                        }
                                    )
                                }

                                else -> {
                                    RegisterScreen(
                                        viewModel = authViewModel,
                                        onRegistered = {
                                            showLogin = true
                                        },
                                        onNavigateToLogin = {
                                            showLogin = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
