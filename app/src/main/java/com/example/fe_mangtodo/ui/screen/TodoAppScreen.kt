package com.example.fe_mangtodo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fe_mangtodo.ui.components.BottomNavigationBar
import com.example.fe_mangtodo.ui.components.DateSelector
import com.example.fe_mangtodo.ui.components.TaskItem
import com.example.fe_mangtodo.ui.components.TaskList

@Composable
fun TodoAppScreen(
    onAddTask: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleTasks = listOf(
        TaskItem("Study", "School", "Pending"),
        TaskItem("Read Book", "Self Dev", "Pending"),
        TaskItem("Finish App", "Work", "Done")
    )

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomNavigationBar(onProfileClick = onProfileClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Oct, 2020", style = MaterialTheme.typography.titleLarge)

            DateSelector()

            Spacer(modifier = Modifier.height(16.dp))
            Text("Task", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))
            TaskList(tasks = sampleTasks)
        }
    }
}
