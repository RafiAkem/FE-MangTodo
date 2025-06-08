package com.example.fe_mangtodo.ui.screen

import BottomNavigationBar
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fe_mangtodo.ui.components.DateSelector
import com.example.fe_mangtodo.ui.components.TaskItem
import com.example.fe_mangtodo.ui.components.TaskList
import com.example.fe_mangtodo.ui.theme.FEMangTodoTheme
import com.example.fe_mangtodo.viewmodel.AuthViewModel
import com.example.fe_mangtodo.viewmodel.CategoryViewModel
import com.example.fe_mangtodo.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.AlertDialog

@RequiresApi(value = 26)
@Composable
fun TodoAppScreen(
    onAddTask: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    username: String,
    userId: String,
    taskViewModel: TaskViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val currentDateFormatted = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("MMM, yyyy", Locale.ENGLISH)
        )
    }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TaskItem?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedDate) {
        taskViewModel.loadUserTasks(userId, selectedDate)
        categoryViewModel.loadUserCategories(userId)
    }

    LaunchedEffect(taskViewModel.deleteTaskState) {
        taskViewModel.deleteTaskState?.onSuccess {
            snackbarHostState.showSnackbar("Task deleted successfully!")
            taskViewModel.resetDeleteTaskState()
        }?.onFailure {
            snackbarHostState.showSnackbar("Failed to delete task: ${it.message}")
            taskViewModel.resetDeleteTaskState()
        }
    }

    val categories = categoryViewModel.categories

    val tasks = taskViewModel.tasks.map { task ->
        val categoryName = categories.find { it.id == task.categoryId }?.name
        TaskItem(
            id = task.id,
            title = task.title,
            description = task.description,
            dueDate = LocalDate.parse(task.dueDate.substring(0, 10)),
            dueTime = LocalTime.parse(task.dueTime),
            status = task.status,
            categoryName = categoryName
        )
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
            ) {
                BottomNavigationBar(
                    onHomeClick = {},
                    onProfileClick = onProfileClick,
                    onAddClick = onAddTask,
                    isProfileSelected = false,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "MangTodo",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "What are we going to do today, $username?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(currentDateFormatted, style = MaterialTheme.typography.titleLarge)

            DateSelector(onDateSelected = { date -> selectedDate = date })

            Spacer(modifier = Modifier.height(16.dp))
            Text("Task", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))
            
            if (taskViewModel.isLoading || categoryViewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                TaskList(
                    tasks = tasks,
                    onEditTask = { task ->
                        // TODO: Implement navigation to AddTaskScreen for editing
                        // For now, let's just log the task to be edited
                        println("Edit Task: ${task.title}")
                    },
                    onDeleteTask = { task ->
                        taskToDelete = task
                        showDeleteConfirmationDialog = true
                    }
                )
            }
        }
    }

    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete \"${taskToDelete?.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    taskToDelete?.let { task ->
                        taskViewModel.deleteTask(task.id, userId)
                    }
                    showDeleteConfirmationDialog = false
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@RequiresApi(value = 26)
@Preview(showBackground = true)
@Composable
fun TodoAppScreenPreview() {
    FEMangTodoTheme {
        TodoAppScreen(
            onAddTask = {},
            onProfileClick = {},
            onLogout = {},
            modifier = Modifier.fillMaxSize(),
            username = "Akem",
            userId = "preview-user-id"
        )
    }
}
