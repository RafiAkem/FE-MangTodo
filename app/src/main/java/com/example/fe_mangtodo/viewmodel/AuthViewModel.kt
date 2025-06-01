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

    private var _currentUsername by mutableStateOf<String?>(null)
    val currentUsername: String get() = _currentUsername ?: "User"

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(email, password))
                loginState = Result.success(response)
                //Store Username
                _currentUsername = response.data.user.name
            } catch (e: Exception) {
                loginState = Result.failure(e)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.register(RegisterRequest(name, email, password))
                registerState = Result.success(response)
                //Store username
                _currentUsername = name
            } catch (e: Exception) {
                registerState = Result.failure(e)
            }
        }
    }

    fun logout() {
        loginState = null
        _currentUsername = null
    }
}