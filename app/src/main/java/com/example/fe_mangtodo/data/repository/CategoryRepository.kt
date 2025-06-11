package com.example.fe_mangtodo.data.repository

import android.util.Log
import com.example.fe_mangtodo.data.local.dao.CategoryDao
import com.example.fe_mangtodo.data.local.entity.CategoryEntity
import com.example.fe_mangtodo.data.mapper.toEntity
import com.example.fe_mangtodo.data.mapper.toCategory
import com.example.fe_mangtodo.data.model.Category
import com.example.fe_mangtodo.data.model.CategoryRequest
import com.example.fe_mangtodo.data.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val dao: CategoryDao,
    private val apiService: ApiService,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "CategoryRepository"

    fun getCategories(userId: String): Flow<List<Category>> {
        Log.d(TAG, "Getting categories from local database for user: $userId")
        // First return local data immediately
        val localFlow = dao.getAllCategories(userId).map { entities ->
            Log.d(TAG, "Retrieved ${entities.size} categories from local database")
            entities.map { it.toCategory() }
        }

        // Then attempt to sync in the background
        coroutineScope.launch {
            try {
                syncCategories(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Background sync failed: ${e.message}")
                // Don't throw the error since we're in background
            }
        }

        return localFlow
    }

    suspend fun syncCategories(userId: String) {
        try {
            Log.d(TAG, "Syncing categories from remote for user: $userId")
            val response = apiService.getUserCategories(userId)
            val categories = response.data
            Log.d(TAG, "Retrieved ${categories.size} categories from remote")
            
            val entities = categories.map { it.toEntity() }
            Log.d(TAG, "Converting ${entities.size} categories to entities")
            dao.insertCategories(entities)
            Log.d(TAG, "Saved ${entities.size} categories to local database")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing categories: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            // Don't throw the error, just log it and continue with local data
        }
    }

    suspend fun createCategory(request: CategoryRequest): Category {
        try {
            Log.d(TAG, "Creating category with request: $request")
            val response = apiService.createCategory(request)
            Log.d(TAG, "API Response: $response")

            if (response.status == "success") {
                Log.d(TAG, "Category created successfully on backend")
                val category = response.data
                // Save to local database
                dao.insertCategory(category.toEntity())
                return category
            } else {
                Log.e(TAG, "Failed to create category: Backend did not return success status")
                throw Exception("Failed to create category: Backend did not return success status.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating category: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }

    suspend fun deleteCategory(categoryId: String, userId: String) {
        try {
            Log.d(TAG, "Deleting category: $categoryId")
            
            apiService.deleteCategory(categoryId, userId)
            Log.d(TAG, "Category deleted from remote, removing from local database")
            dao.deleteById(categoryId)
            Log.d(TAG, "Category removed from local database")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting category: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }
} 