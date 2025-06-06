package com.example.fe_mangtodo.ui.screen

import BottomNavigationBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fe_mangtodo.ui.components.DateSelector
import com.example.fe_mangtodo.ui.components.TaskItem
import com.example.fe_mangtodo.ui.components.TaskList
import androidx.compose.ui.tooling.preview.Preview
import com.example.fe_mangtodo.ui.theme.FEMangTodoTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(value = 26)
@Composable
fun TodoAppScreen(
    onAddTask: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    username: String
) {
    val currentDate = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("MMM, yyyy", Locale.ENGLISH)
        )
    }

    val sampleTasks = listOf(
        TaskItem("Study", "School", "pending"),
        TaskItem("Read Book", "Self Dev", "pending"),
        TaskItem("Finish App", "Work", "done")
    )

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
        }
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
            Text(currentDate, style = MaterialTheme.typography.titleLarge)

            DateSelector()

            Spacer(modifier = Modifier.height(16.dp))
            Text("Task", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))
            TaskList(tasks = sampleTasks)
        }
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
            username = "Akem"
        )
    }
}
