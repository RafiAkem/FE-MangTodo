package com.example.fe_mangtodo.ui.components

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
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val dueTime: LocalTime,
    val status: String,
    val categoryName: String?
)

@Composable
fun TaskList(
    tasks: List<TaskItem>,
    modifier: Modifier = Modifier
) {
    var selectedTask by remember { mutableStateOf<TaskItem?>(null) }

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
                onClick = { selectedTask = task }
            )
        }
    }

    selectedTask?.let { task ->
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        AlertDialog(
            onDismissRequest = { selectedTask = null },
            title = { Text(task.title) },
            text = {
                Column {
                    Text(
                        text = "Due: ${task.dueDate.format(dateFormatter)} at ${task.dueTime.format(timeFormatter)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal
                    )
                    task.categoryName?.let {
                        Text(
                            text = "Category: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTask = null }) {
                    Text("OK")
                }
            }
        )
    }
}
