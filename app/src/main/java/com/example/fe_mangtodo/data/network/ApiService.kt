package com.example.fe_mangtodo.data.network

import com.example.fe_mangtodo.data.model.LoginRequest
import com.example.fe_mangtodo.data.model.LoginResponse
import com.example.fe_mangtodo.data.model.RegisterRequest
import com.example.fe_mangtodo.data.model.RegisterResponse
import com.example.fe_mangtodo.data.model.TaskRequest
import com.example.fe_mangtodo.data.model.TaskResponse
import com.example.fe_mangtodo.data.model.CategoryRequest
import com.example.fe_mangtodo.data.model.CategoryResponse
import com.example.fe_mangtodo.data.model.CategoriesResponse
import com.example.fe_mangtodo.data.model.TasksResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("tasks")
    suspend fun createTask(@Body taskRequest: TaskRequest): TaskResponse

    @GET("categories")
    suspend fun getUserCategories(@Query("userId") userId: String): CategoriesResponse

    @POST("categories")
    suspend fun createCategory(@Body categoryRequest: CategoryRequest): CategoryResponse

    @GET("tasks")
    suspend fun getUserTasks(@Query("userId") userId: String): TasksResponse

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") taskId: String, @Query("userId") userId: String): Unit

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") taskId: String, @Body taskRequest: TaskRequest): TaskResponse
}


