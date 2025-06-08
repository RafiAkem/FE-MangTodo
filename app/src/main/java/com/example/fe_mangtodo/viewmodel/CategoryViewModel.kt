package com.example.fe_mangtodo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe_mangtodo.data.model.Category
import com.example.fe_mangtodo.data.model.CategoryRequest
import com.example.fe_mangtodo.data.model.CategoryResponse
import com.example.fe_mangtodo.data.network.RetrofitClient
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var createCategoryState by mutableStateOf<Result<CategoryResponse>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadUserCategories(userId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.getUserCategories(userId)
                categories = response.data
            } catch (e: Exception) {
                println("Error loading categories: ${e.message}")
                categories = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun createCategory(name: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.createCategory(CategoryRequest(name, userId))
                createCategoryState = Result.success(response)
                // Reload categories after creating a new one
                loadUserCategories(userId)
            } catch (e: Exception) {
                println("Error creating category: ${e.message}")
                createCategoryState = Result.failure(e)
            }
        }
    }

    fun resetCreateCategoryState() {
        createCategoryState = null
    }
}
