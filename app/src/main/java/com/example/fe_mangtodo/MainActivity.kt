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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.fe_mangtodo.ui.screen.*
import com.example.fe_mangtodo.ui.screen.AddTaskScreen
import com.example.fe_mangtodo.ui.screen.CategoryManagementScreen
import com.example.fe_mangtodo.ui.screen.LoginScreen
import com.example.fe_mangtodo.ui.screen.ProfileScreen
import com.example.fe_mangtodo.ui.screen.RegisterScreen
import com.example.fe_mangtodo.ui.screen.SplashScreen
import com.example.fe_mangtodo.ui.screen.TodoAppScreen
import com.example.fe_mangtodo.ui.theme.FEMangTodoTheme
import com.example.fe_mangtodo.viewmodel.AuthViewModel
import com.example.fe_mangtodo.viewmodel.CategoryViewModel
import com.example.fe_mangtodo.viewmodel.TaskViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
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
                var showEditProfile by remember { mutableStateOf(false) }
                var showChangePassword by remember { mutableStateOf(false) }

                var isNavigatingForward by remember { mutableStateOf(true) }

                val taskViewModel: TaskViewModel = hiltViewModel()
                val authViewModel: AuthViewModel = hiltViewModel()
                val categoryViewModel: CategoryViewModel = hiltViewModel()


                if (showSplash) {
                    SplashScreen(onNavigateToMain = { showSplash = false })
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val targetScreen = when {
                            showEditProfile -> "editProfile"
                            showChangePassword -> "changePassword"
                            showAddTask -> "addtask"
                            showProfile -> "profile"
                            showCategoryManagement -> "category"
                            isAuthenticated -> "home"
                            showLogin -> "login"
                            else -> "register"
                        }

                        AnimatedContent(
                            targetState = targetScreen,
                            transitionSpec = {
                                when (targetState) {
                                    "addtask" -> fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                                    else -> {
                                        if (isNavigatingForward) {
                                            slideInHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth } + fadeIn() with
                                                    slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth } + fadeOut()
                                        } else {
                                            slideInHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth } + fadeIn() with
                                                    slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth } + fadeOut()
                                        }
                                    }
                                }.using(SizeTransform(clip = false))
                            }
                        ) { screen ->
                            when (screen) {
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
                                        onProfileClick = { },
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
                                        },
                                        onEditProfile = {
                                            isNavigatingForward = true
                                            showProfile = false
                                            showEditProfile = true
                                        },
                                        onChangePassword = {
                                            isNavigatingForward = true
                                            showProfile = false
                                            showChangePassword = true
                                        }
                                    )
                                }

                                "editProfile" -> {
                                    EditProfileScreen(
                                        viewModel = authViewModel,
                                        onBack = {
                                            isNavigatingForward = false
                                            showEditProfile = false
                                            showProfile = true
                                        }
                                    )
                                }

                                "changePassword" -> {
                                    ChangePasswordScreen(
                                        viewModel = authViewModel,
                                        onBack = {
                                            isNavigatingForward = false
                                            showChangePassword = false
                                            showProfile = true
                                        }
                                    )
                                }

                                "category" -> {
                                    CategoryManagementScreen(
                                        userId = authViewModel.currentUserId ?: "",
                                        onNavigateBack = {
                                            isNavigatingForward = false
                                            showCategoryManagement = false
                                        },
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

                                "register" -> {
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
