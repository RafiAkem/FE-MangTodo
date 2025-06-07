package com.example.fe_mangtodo.ui.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fe_mangtodo.viewmodel.TaskViewModel
import java.time.Instant
import java.time.ZoneId
import androidx.compose.material3.SnackbarHost

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit, //buat balik ke screen
    onTaskAdded: () -> Unit, //callback pas task
    userId: String,
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel = viewModel() //buat akses fungsi createtask
) {
    //save user input
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(LocalDate.now()) } //default tgl hari ini
    var showDatePicker by remember { mutableStateOf(false) } //kalau tgl diklik
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy") //format tgl yg ditampilin

    //ambil state hasil create task
    val createTaskState = taskViewModel.createTaskState
    //state snackbar, bwt nampilin pesan ke user
    val snackbarHostState = remember { SnackbarHostState() }

    //efeksamping createtaskstate
    LaunchedEffect(createTaskState) {
        createTaskState?.onSuccess {
            //kalo berhasil, tampilin snackbar dan panggil onTaskAdded()
            snackbarHostState.showSnackbar("Task '${it.task.title}' berhasil dibuat!")
            onTaskAdded()
        }?.onFailure {
            //kalo gagal, tampilin snackbar error
            snackbarHostState.showSnackbar("Gagal membuat task: ${it.message}")
        }
    }

    // Date Picker Dialog kalo true
    if (showDatePicker) {
        //State untuk date picker, tanggal yang dipilih default
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )

        //dialog date picker
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false }, //tutup dialog klo mncet yg lain
            confirmButton = {
                TextButton(onClick = {
                    //ambil tgl yg dipilih trs set ke due date
                    datePickerState.selectedDateMillis?.let { millis ->
                        dueDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false //tutup
                }) {
                    Text("OK")
                }
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
            TopAppBar(
                title = { Text("Add New Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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

            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Due Date: ${dueDate.format(dateFormatter)}")
            }

            Spacer(modifier = Modifier.weight(1f))

            //button buat create task
            Button(
                onClick = {
                    //panggil fungsi di createtask
                    taskViewModel.createTask(
                        title = title,
                        description = description,
                        dueDate = dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        status = "pending",
                        userId = userId
                    )
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Add Task")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddTaskScreenPreview() {
    AddTaskScreen(
        onNavigateBack = {},
        onTaskAdded = {},
        userId = "9aa6e445-d490-4793-b263-fa933217d24"
    )
}