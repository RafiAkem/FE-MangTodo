package com.example.fe_mangtodo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe_mangtodo.data.model.LoginRequest
import com.example.fe_mangtodo.data.model.LoginResponse
import com.example.fe_mangtodo.data.model.RegisterRequest
import com.example.fe_mangtodo.data.model.RegisterResponse
import com.example.fe_mangtodo.data.network.RetrofitClient
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var loginState by mutableStateOf<Result<LoginResponse>?>(null)
    var registerState by mutableStateOf<Result<RegisterResponse>?>(null)

    // Add loading states
    var isLoginLoading by mutableStateOf(false)
    var isRegisterLoading by mutableStateOf(false)

    private var _currentUserId by mutableStateOf<String?>(null)
    val currentUserId: String? get() = _currentUserId

    private var _currentUsername by mutableStateOf<String?>(null)
    val currentUsername: String get() = _currentUsername ?: "User"

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoginLoading = true
            try {
                val response = RetrofitClient.api.login(LoginRequest(email, password))
                loginState = Result.success(response)
                //Store Username
                _currentUsername = response.data.user.name
                _currentUserId = response.data.user.id
            } catch (e: Exception) {
                loginState = Result.failure(e)
            } finally {
                isLoginLoading = false
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            isRegisterLoading = true
            try {
                val response = RetrofitClient.api.register(RegisterRequest(name, email, password))
                registerState = Result.success(response)
                login(email, password)
            } catch (e: Exception) {
                registerState = Result.failure(e)
            } finally {
                isRegisterLoading = false
            }
        }
    }

    fun logout() {
        loginState = null
        _currentUsername = null
        _currentUserId = null
    }

    fun isUserLoggedIn(): Boolean {
        return _currentUserId != null
    }
}
