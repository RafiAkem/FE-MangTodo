package com.example.fe_mangtodo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(LocalTime.now().hour) }
    var selectedMinute by remember { mutableStateOf(LocalTime.now().minute) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                NumberPicker(
                    value = selectedHour,
                    onValueChange = { selectedHour = it },
                    range = 0..23,
                    modifier = Modifier.weight(1f)
                )
                Text(":", modifier = Modifier.padding(horizontal = 8.dp))
                NumberPicker(
                    value = selectedMinute,
                    onValueChange = { selectedMinute = it },
                    range = 0..59,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(selectedHour, selectedMinute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
