package com.example.fe_mangtodo.ui.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fe_mangtodo.viewmodel.TaskViewModel
import com.example.fe_mangtodo.ui.icons.Schedule
import com.example.fe_mangtodo.ui.components.CurvedBottomShape
import com.example.fe_mangtodo.ui.components.TimePickerDialog

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    onTaskAdded: () -> Unit,
    userId: String,
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(LocalDate.now()) }
    var dueTime by remember { mutableStateOf(LocalTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val displayDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val displayTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val apiDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val snackbarHostState = remember { SnackbarHostState() }
    val createTaskState = taskViewModel.createTaskState

    LaunchedEffect(createTaskState) {
        createTaskState?.onSuccess {
            snackbarHostState.showSnackbar("Task created successfully!")
            onTaskAdded()
        }?.onFailure {
            snackbarHostState.showSnackbar("Failed to create task: ${it.message}")
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { hour, minute ->
                dueTime = LocalTime.of(hour, minute)
                showTimePicker = false
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dueDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(CurvedBottomShape())
                    .background(Color(0xFF003399)) // Dark Blue
            ) {
                // Back button on the left
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                // Center title
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "New Task",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = dueDate.format(displayDateFormatter),
                    onValueChange = {},
                    label = { Text("Due Date") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, "Select Date")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = dueTime.format(displayTimeFormatter),
                    onValueChange = {},
                    label = { Text("Time") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Schedule, "Select Time")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    taskViewModel.createTask(
                        title = title,
                        description = description,
                        dueDate = dueDate.format(apiDateFormatter),
                        dueTime = dueTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        userId = userId
                    )
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Create Task")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddTaskScreenPreview() {
    MaterialTheme {
        AddTaskScreen(
            onNavigateBack = {},
            onTaskAdded = {},
            userId = "preview-user-id"
        )
    }
}
