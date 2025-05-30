package com.example.fe_mangtodo.data.model

data class LoginResponse(val token: String, val user: User)
data class RegisterResponse(val message: String)
data class User(val id: Int, val name: String, val email: String)