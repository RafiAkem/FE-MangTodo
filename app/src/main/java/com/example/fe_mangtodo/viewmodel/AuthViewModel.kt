package com.example.fe_mangtodo.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe_mangtodo.data.model.LoginRequest
import com.example.fe_mangtodo.data.model.LoginResponse
import com.example.fe_mangtodo.data.model.RegisterRequest
import com.example.fe_mangtodo.data.model.RegisterResponse
import com.example.fe_mangtodo.data.model.UpdateUsernameRequest
import com.example.fe_mangtodo.data.model.UpdatePasswordRequest
import com.example.fe_mangtodo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val TAG = "AuthViewModel"

    var loginState by mutableStateOf<Result<LoginResponse>?>(null)
        private set
    var registerState by mutableStateOf<Result<RegisterResponse>?>(null)
        private set
    var updateUsernameState by mutableStateOf<Result<Unit>?>(null)
    var updatePasswordState by mutableStateOf<Result<Unit>?>(null)
    var profileErrorMessage by mutableStateOf<String?>(null)
        private set

    // Add loading states
    var isLoginLoading by mutableStateOf(false)
        private set
    var isRegisterLoading by mutableStateOf(false)
        private set

    private var _currentUserId by mutableStateOf<String?>(null)
    val currentUserId: String? get() = _currentUserId

    private var _currentUsername by mutableStateOf<String?>(null)
    val currentUsername: String get() = _currentUsername ?: "User"

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoginLoading = true
            try {
                Log.d(TAG, "Attempting to login with email: $email")
                val result = repository.login(email, password)
                result.onSuccess { response ->
                    loginState = Result.success(response)
                    _currentUsername = response.data.user.name
                    _currentUserId = response.data.user.id
                    Log.d(TAG, "Login successful for user: ${response.data.user.name}")
                }.onFailure { error ->
                    loginState = Result.failure(error)
                    Log.e(TAG, "Login failed: ${error.message}")
                }
            } catch (e: Exception) {
                loginState = Result.failure(e)
                Log.e(TAG, "Login error: ${e.message}")
            } finally {
                isLoginLoading = false
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            isRegisterLoading = true
            try {
                Log.d(TAG, "Attempting to register user: $name")
                val result = repository.register(name, email, password)
                result.onSuccess { response ->
                    registerState = Result.success(response)
                    Log.d(TAG, "Registration successful for user: $name")
                    // Auto login after successful registration
                    login(email, password)
                }.onFailure { error ->
                    registerState = Result.failure(error)
                    Log.e(TAG, "Registration failed: ${error.message}")
                }
            } catch (e: Exception) {
                registerState = Result.failure(e)
                Log.e(TAG, "Registration error: ${e.message}")
            } finally {
                isRegisterLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _currentUserId?.let { userId ->
                    repository.logout(userId)
                }
                loginState = null
                _currentUsername = null
                _currentUserId = null
                Log.d(TAG, "Logout successful")
            } catch (e: Exception) {
                Log.e(TAG, "Logout error: ${e.message}")
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return _currentUserId != null
    }

    fun updateUsername(userId: String, newName: String, currentPassword: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Attempting to update username to: $newName")
                val result = repository.updateUsername(userId, newName, currentPassword)
                result.onSuccess {
                    updateUsernameState = Result.success(Unit)
                    _currentUsername = newName
                    Log.d(TAG, "Username updated successfully")
                }.onFailure { error ->
                    updateUsernameState = Result.failure(error)
                    profileErrorMessage = error.message
                    Log.e(TAG, "Username update failed: ${error.message}")
                }
            } catch (e: Exception) {
                updateUsernameState = Result.failure(e)
                profileErrorMessage = e.message
                Log.e(TAG, "Username update error: ${e.message}")
            }
        }
    }

    fun updatePassword(userId: String, currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Attempting to update password")
                val result = repository.updatePassword(userId, currentPassword, newPassword)
                result.onSuccess {
                    updatePasswordState = Result.success(Unit)
                    Log.d(TAG, "Password updated successfully")
                }.onFailure { error ->
                    updatePasswordState = Result.failure(error)
                    profileErrorMessage = error.message
                    Log.e(TAG, "Password update failed: ${error.message}")
                }
            } catch (e: Exception) {
                updatePasswordState = Result.failure(e)
                profileErrorMessage = e.message
                Log.e(TAG, "Password update error: ${e.message}")
            }
        }
    }
}
