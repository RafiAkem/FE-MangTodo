package com.example.fe_mangtodo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class TaskItem(
    val title: String,
    val subtitle: String,
    val status: String
)

@Composable
fun TaskList(
    tasks: List<TaskItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) { task ->
            TaskCard(
                title = task.title,
                subtitle = task.subtitle,
                status = task.status
            )
        }
    }
}
