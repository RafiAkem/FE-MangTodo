package com.example.fe_mangtodo.data.mapper

import com.example.fe_mangtodo.data.local.entity.CategoryEntity
import com.example.fe_mangtodo.data.model.Category

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        userId = userId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        userId = userId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 