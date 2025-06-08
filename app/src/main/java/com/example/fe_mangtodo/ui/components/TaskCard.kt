package com.example.fe_mangtodo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskCard(
    title: String,
    description: String,
    dueDate: LocalDate,
    dueTime: LocalTime,
    status: String,
    categoryName: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (backgroundColor, statusText, statusColor) = when (status.lowercase()) {
        "complete" -> Triple(
            Color(0xFFE8F5E9),
            "Completed",
            Color(0xFF2E7D32)
        )
        "in_progress" -> Triple(
            Color(0xFFFFF8E1),
            "In Progress",
            Color(0xFFF9A825)
        )
        "late" -> Triple(
            Color(0xFFFFEBEE),
            "Overdue",
            Color(0xFFD32F2F)
        )
        else -> Triple(
            Color(0xFFE0E0E0),
            "Unknown",
            Color(0xFF757575)
        )
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dueDate.format(dateFormatter),
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "at ${dueTime.format(timeFormatter)}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
                categoryName?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary, // Or any other suitable color
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Text(
                text = statusText,
                color = statusColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
