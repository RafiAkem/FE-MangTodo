package com.example.fe_mangtodo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class TaskItem(
    val id: String,
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val dueTime: LocalTime,
    val status: String,
    val categoryName: String?
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList(
    tasks: List<TaskItem>,
    modifier: Modifier = Modifier,
    onEditTask: (TaskItem) -> Unit,
    onDeleteTask: (TaskItem) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(tasks) { task ->
            TaskCard(
                title = task.title,
                description = task.description,
                dueDate = task.dueDate,
                dueTime = task.dueTime,
                status = task.status,
                categoryName = task.categoryName,
                onEditClick = { onEditTask(task) },
                onDeleteClick = { onDeleteTask(task) }
            )
        }
    }
}
