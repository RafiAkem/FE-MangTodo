package com.example.fe_mangtodo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import kotlin.text.get
import kotlin.text.set

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
    onDeleteTask: (TaskItem) -> Unit,
    onStatusChange: (TaskItem, String) -> Unit
) {
    val checkedTasks = remember { mutableStateMapOf<String, Boolean>() }
    val taskStatuses = remember { mutableStateMapOf<String, String>().apply {
        tasks.forEach { put(it.id, it.status) }
    } }
    val previousStatuses = remember { mutableStateMapOf<String, String>() }
    var showDialogForTaskId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            var visible by remember { mutableStateOf(true) }
            val checked = checkedTasks[task.id] ?: (task.status == "complete")
            val status = taskStatuses[task.id] ?: task.status

            if (showDialogForTaskId == task.id) {
                AlertDialog(
                    onDismissRequest = { showDialogForTaskId = null },
                    title = { Text("Confirm Completion") },
                    text = { Text("Are you sure this task is complete?") },
                    confirmButton = {
                        TextButton(onClick = {
                            previousStatuses[task.id] = status
                            checkedTasks[task.id] = true
                            taskStatuses[task.id] = "complete"
                            showDialogForTaskId = null
                        }) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            checkedTasks[task.id] = false
                            showDialogForTaskId = null
                        }) { Text("No") }
                    }
                )
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TaskCard(
                    title = task.title,
                    description = task.description,
                    dueDate = task.dueDate,
                    dueTime = task.dueTime,
                    status = status,
                    categoryName = task.categoryName,
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        if (isChecked && status != "complete") {
                            showDialogForTaskId = task.id
                        } else if (!isChecked && status == "complete") {
                            // Restore previous status if available, else fallback to original
                            val prevStatus = previousStatuses[task.id] ?: task.status
                            checkedTasks[task.id] = false
                            taskStatuses[task.id] = prevStatus
                            onStatusChange(task, prevStatus)
                        }
                    },
                    modifier = Modifier.animateItem(
                        fadeInSpec = null,
                        fadeOutSpec = null
                    ),
                    onEditClick = { onEditTask(task) },
                    onDeleteClick = {
                        visible = false
                        onDeleteTask(task)
                    },
                    onStatusChange = { newStatus ->
                        if (newStatus == "complete") {
                            showDialogForTaskId = task.id
                        } else {
                            taskStatuses[task.id] = newStatus
                            checkedTasks[task.id] = false
                            onStatusChange(task, newStatus)
                        }
                    }
                )
            }
        }
    }
}
