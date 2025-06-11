package com.example.fe_mangtodo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(value = 26)
@Composable
fun DateSelector(
    onDateSelected: (LocalDate?) -> Unit
) {
    val today = LocalDate.now()
    val dates = (0..3).map { offset ->
        today.plusDays(offset.toLong())
    }
    val displayDates = listOf<LocalDate?>(null) + dates
    var selectedIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { // Initialize with "All" selected
        onDateSelected(displayDates[0])
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        displayDates.forEachIndexed { index, date ->
            val isSelected = selectedIndex == index
            val textForDate = if (date == null) "All" else date.dayOfMonth.toString()
            val textForDay = if (date == null) "" else date.format(DateTimeFormatter.ofPattern("E", Locale.ENGLISH))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFDDE1F3),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .weight(1f)
                    .clickable {
                        selectedIndex = index
                        onDateSelected(date)
                    }
            ) {
                Text(
                    text = textForDate,
                    fontSize = 16.sp,
                    color = if (isSelected) Color.White else Color.Black
                )
                if (date != null) { // Only show day of week for actual dates
                    Text(
                        text = textForDay,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            }
        }
    }
}