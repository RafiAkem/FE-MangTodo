package com.example.fe_mangtodo.data.repository

import android.util.Log
import com.example.fe_mangtodo.data.local.dao.UserDao
import com.example.fe_mangtodo.data.local.entity.UserEntity
import com.example.fe_mangtodo.data.mapper.toEntity
import com.example.fe_mangtodo.data.model.LoginRequest
import com.example.fe_mangtodo.data.model.LoginResponse
import com.example.fe_mangtodo.data.model.LoginData
import com.example.fe_mangtodo.data.model.User
import com.example.fe_mangtodo.data.model.RegisterRequest
import com.example.fe_mangtodo.data.model.RegisterResponse
import com.example.fe_mangtodo.data.model.UpdateUsernameRequest
import com.example.fe_mangtodo.data.model.UpdatePasswordRequest
import com.example.fe_mangtodo.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val dao: UserDao,
    private val apiService: ApiService
) {
    private val TAG = "AuthRepository"

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        Log.d(TAG, "Starting login process for email: $email")
        
        return try {
            Log.d(TAG, "Attempting online login...")
            // Try to login online first
            val response = apiService.login(LoginRequest(email, password))
            Log.d(TAG, "Online login successful")
            
            // If successful, save user data locally
            val userEntity = UserEntity(
                id = response.data.user.id,
                name = response.data.user.name,
                email = email,
                password = password,
                token = response.data.token
            )
            Log.d(TAG, "Saving user data to local database")
            dao.insertUser(userEntity)
            Log.d(TAG, "User data saved successfully")
            
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Online login failed: ${e.message}")
            Log.d(TAG, "Falling back to offline login...")
            
            // If online login fails, try offline login
            try {
                Log.d(TAG, "Checking local database for user")
                val user = dao.getUserByEmail(email)
                
                if (user != null) {
                    Log.d(TAG, "User found in local database")
                    if (user.password == password) {
                        Log.d(TAG, "Password matches, proceeding with offline login")
                        // Create a response similar to the online one
                        val offlineResponse = LoginResponse(
                            status = 200, // Success status code
                            message = "Offline login successful",
                            data = LoginData(
                                user = User(
                                    id = user.id,
                                    name = user.name,
                                    email = user.email
                                ),
                                token = user.token ?: ""
                            )
                        )
                        Log.d(TAG, "Offline login successful for user: ${user.name}")
                        Result.success(offlineResponse)
                    } else {
                        Log.e(TAG, "Password mismatch for offline login")
                        Result.failure(Exception("Invalid credentials"))
                    }
                } else {
                    Log.e(TAG, "User not found in local database")
                    Result.failure(Exception("User not found"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Offline login failed: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                Result.failure(e)
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            
            // If successful, save user data locally with a temporary ID
            val userEntity = UserEntity(
                id = "temp_${System.currentTimeMillis()}", // Temporary ID until login
                name = name,
                email = email,
                password = password,
                token = null // Token will be set after login
            )
            dao.insertUser(userEntity)
            
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateUsername(userId: String, newName: String, currentPassword: String): Result<Unit> {
        return try {
            // Try to update online first
            apiService.updateUsername(userId, UpdateUsernameRequest(newName))
            
            // If successful, update local data
            val user = dao.getUserById(userId)
            if (user != null && user.password == currentPassword) {
                val updatedUser = user.copy(name = newName)
                dao.insertUser(updatedUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update username failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updatePassword(userId: String, currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            // Try to update online first
            apiService.updatePassword(userId, UpdatePasswordRequest(currentPassword, newPassword))
            
            // If successful, update local data
            val user = dao.getUserById(userId)
            if (user != null && user.password == currentPassword) {
                val updatedUser = user.copy(password = newPassword)
                dao.insertUser(updatedUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update password failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun logout(userId: String) {
        try {
            val user = dao.getUserById(userId)
            if (user != null) {
                // Clear the token
                val updatedUser = user.copy(token = null)
                dao.insertUser(updatedUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Logout failed: ${e.message}")
        }
    }

    suspend fun getCurrentUser(userId: String): UserEntity? {
        return try {
            dao.getUserById(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Get current user failed: ${e.message}")
            null
        }
    }
} 