package com.example.fe_mangtodo.data.network

import com.example.fe_mangtodo.data.model.LoginRequest
import com.example.fe_mangtodo.data.model.LoginResponse
import com.example.fe_mangtodo.data.model.RegisterRequest
import com.example.fe_mangtodo.data.model.RegisterResponse
import com.example.fe_mangtodo.data.model.TaskRequest
import com.example.fe_mangtodo.data.model.TaskResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("tasks")
    suspend fun createTask(@Body taskRequest: TaskRequest): TaskResponse
}

