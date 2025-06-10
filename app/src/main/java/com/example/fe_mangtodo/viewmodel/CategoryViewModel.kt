package com.example.fe_mangtodo.viewmodel

import android.util.Log
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
    private val TAG = "CategoryViewModel"

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var createCategoryState by mutableStateOf<Result<CategoryResponse>?>(null)
        private set

    var deleteCategoryState by mutableStateOf<Result<Unit>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadUserCategories(userId: String) {
        if (userId.isBlank()) {
            Log.e(TAG, "Error: userId is empty")
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d(TAG, "Loading categories for user: $userId")
                val response = RetrofitClient.api.getUserCategories(userId)
                categories = response.data
                Log.d(TAG, "Categories loaded: ${categories.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading categories: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                categories = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun createCategory(name: String, userId: String) {
        if (userId.isBlank()) {
            Log.e(TAG, "Error: userId is empty")
            return
        }
        viewModelScope.launch {
            try {
                Log.d(TAG, "Creating category: $name for user: $userId")
                val response = RetrofitClient.api.createCategory(CategoryRequest(name, userId))
                createCategoryState = Result.success(response)
                Log.d(TAG, "Category created successfully: ${response.message}")
                // Reload categories after creating a new one
                loadUserCategories(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating category: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                createCategoryState = Result.failure(e)
            }
        }
    }

    fun deleteCategory(categoryId: String, userId: String) {
        if (userId.isBlank()) {
            Log.e(TAG, "Error: userId is empty")
            return
        }
        viewModelScope.launch {
            try {
                Log.d(TAG, "Deleting category: $categoryId for user: $userId")
                RetrofitClient.api.deleteCategory(categoryId, userId)
                deleteCategoryState = Result.success(Unit)
                Log.d(TAG, "Category deleted successfully")
                // Reload categories after deleting one
                loadUserCategories(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting category: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                deleteCategoryState = Result.failure(e)
            }
        }
    }

    fun resetCreateCategoryState() {
        createCategoryState = null
    }

    fun resetDeleteCategoryState() {
        deleteCategoryState = null
    }
}
