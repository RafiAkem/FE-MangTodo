package com.example.fe_mangtodo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
fun DateSelector() {
    val today = LocalDate.now()
    val dates = (0..3).map { offset ->
        today.plusDays(offset.toLong())
    }
    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        dates.forEachIndexed { index, date ->
            val isSelected = selectedIndex == index
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFDDE1F3),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 16.sp,
                    color = if (isSelected) Color.White else Color.Black
                )
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("E", Locale.ENGLISH)),
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}