package com.example.fe_mangtodo.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // READ - Ambil semua task
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    // CREATE - Tambahkan task
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    // UPDATE - Perbarui task
    @Update
    suspend fun updateTask(task: TaskEntity)

    // DELETE - Hapus task
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // READ (opsional) - Ambil task berdasarkan id
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): TaskEntity?
}
