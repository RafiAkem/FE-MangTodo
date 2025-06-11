package com.example.fe_mangtodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String
) 