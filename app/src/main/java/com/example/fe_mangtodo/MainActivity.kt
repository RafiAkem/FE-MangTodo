package com.example.fe_mangtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fe_mangtodo.ui.screen.LoginScreen
import com.example.fe_mangtodo.ui.screen.RegisterScreen
import com.example.fe_mangtodo.ui.screen.TodoAppScreen
import com.example.fe_mangtodo.ui.theme.FEMangTodoTheme
import com.example.fe_mangtodo.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FEMangTodoTheme {
                var showLogin by remember { mutableStateOf(true) }
                var isAuthenticated by remember { mutableStateOf(false) }
                val authViewModel = remember { AuthViewModel() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isAuthenticated) {
                        TodoAppScreen(
                            onAddTask = { /* Implement add task logic */ },
                            onProfileClick = { /* Implement profile click logic */ },
                            onLogout = {
                                isAuthenticated = false
                                showLogin = true
                            },
                            modifier = Modifier.padding(innerPadding)  // Use innerPadding here
                        )
                    } else {
                        if (showLogin) {
                            LoginScreen(
                                viewModel = authViewModel,
                                onSuccess = { isAuthenticated = true }
                            )
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