package com.example.fe_mangtodo.data.model

data class CategoryResponse(
    val status: String,
    val message: String,
    val data: Category
)

data class CategoriesResponse(
    val status: String,
    val message: String,
    val data: List<Category>
)

data class Category(
    val id: String,
    val name: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String
)

data class CategoryRequest(
    val name: String,
    val userId: String
)
