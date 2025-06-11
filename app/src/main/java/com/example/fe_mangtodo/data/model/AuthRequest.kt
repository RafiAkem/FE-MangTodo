package com.example.fe_mangtodo.data.model

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class UpdateUsernameRequest(
    val name: String
)

data class UpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)