package com.example.fe_mangtodo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fe_mangtodo.viewmodel.AuthViewModel
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var newUsername by remember { mutableStateOf(viewModel.currentUsername) }
    var currentPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val userId = viewModel.currentUserId ?: ""
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.updateUsernameState) {
        viewModel.updateUsernameState?.let {
            if (it.isSuccess) {
                showSuccess = true
            } else if (it.isFailure) {
                errorMessage = viewModel.profileErrorMessage ?: "Failed to update username."
            }
            viewModel.updateUsernameState = null
        }
    }
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {
                showSuccess = false
                onBack()
            },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    onBack()
                }) {
                    Text("OK")
                }
            },
            title = { Text("Success") },
            text = { Text("Username updated successfully!") }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("New Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    errorMessage = null
                    if (newUsername.isBlank()) {
                        errorMessage = "Username cannot be empty."
                    } else if (currentPassword.isBlank()) {
                        errorMessage = "Current password is required."
                    } else if (newUsername == viewModel.currentUsername) {
                        errorMessage = "New username must be different."
                    } else {
                        viewModel.updateUsername(userId, newUsername, currentPassword)
                    }
                },
                enabled = newUsername.isNotBlank() && currentPassword.isNotBlank() && newUsername != viewModel.currentUsername,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 