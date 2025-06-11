package com.example.fe_mangtodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password: String, // Note: In a production app, we should store a hashed password
    val token: String?
) 