package com.example.fe_mangtodo

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.fe_mangtodo.data.local.db.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MangTodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Clean up old database versions
        cleanupOldDatabases()
    }

    private fun cleanupOldDatabases() {
        val oldDbNames = listOf(
            "mangtodo_db",
        )
        
        oldDbNames.forEach { dbName ->
            try {
                deleteDatabase(dbName)
            } catch (e: Exception) {
                // Ignore errors during cleanup
            }
        }
    }
}

