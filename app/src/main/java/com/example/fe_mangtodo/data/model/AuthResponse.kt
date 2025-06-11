package com.example.fe_mangtodo.data.model

data class LoginResponse(
    val status: Int,
    val message: String,
    val data: LoginData
)

data class LoginData(
    val user: User,
    val token: String
)

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class RegisterResponse(
    val message: String
)