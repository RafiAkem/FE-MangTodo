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
import com.example.fe_mangtodo.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {
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
                // First sync with remote
                repository.syncCategories(userId)
                
                // Then observe local data
                repository.getCategories(userId).collect { allCategories ->
                    categories = allCategories
                    Log.d(TAG, "Categories loaded: ${categories.size}")
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading categories: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                categories = emptyList()
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
            isLoading = true
            try {
                Log.d(TAG, "Creating category: $name for user: $userId")
                val category = repository.createCategory(CategoryRequest(name, userId))
                createCategoryState = Result.success(CategoryResponse("success", "Category created", category))
                Log.d(TAG, "Category created successfully")
                // Reload categories after creating a new one
                loadUserCategories(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating category: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                createCategoryState = Result.failure(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCategory(categoryId: String, userId: String) {
        if (userId.isBlank()) {
            Log.e(TAG, "Error: userId is empty")
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d(TAG, "Deleting category: $categoryId for user: $userId")
                repository.deleteCategory(categoryId, userId)
                deleteCategoryState = Result.success(Unit)
                Log.d(TAG, "Category deleted successfully")
                // Reload categories after deleting one
                loadUserCategories(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting category: ${e.message}")
                Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                deleteCategoryState = Result.failure(e)
            } finally {
                isLoading = false
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
