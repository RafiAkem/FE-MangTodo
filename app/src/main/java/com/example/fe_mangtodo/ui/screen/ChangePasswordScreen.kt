package com.example.fe_mangtodo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fe_mangtodo.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val userId = viewModel.currentUserId ?: ""
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.updatePasswordState) {
        viewModel.updatePasswordState?.let {
            if (it.isSuccess) {
                showSuccess = true
            } else if (it.isFailure) {
                errorMessage = viewModel.profileErrorMessage ?: "Failed to update password."
            }
            viewModel.updatePasswordState = null
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
            text = { Text("Password updated successfully!") }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password") },
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
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
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
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
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
                    when {
                        currentPassword.isBlank() -> errorMessage = "Current password is required."
                        newPassword.isBlank() -> errorMessage = "New password cannot be empty."
                        newPassword.length < 6 -> errorMessage = "Password must be at least 6 characters."
                        newPassword != confirmPassword -> errorMessage = "Passwords do not match."
                        else -> viewModel.updatePassword(userId, currentPassword, newPassword)
                    }
                },
                enabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && newPassword == confirmPassword && newPassword.length >= 6,
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