package com.example.fe_mangtodo.ui.components

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

@Composable
fun DateSelector() {
    val dates = listOf("10" to "Mon", "11" to "Tue", "12" to "Wed", "13" to "Thu")
    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        dates.forEachIndexed { index, pair ->
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
                    text = pair.first,
                    fontSize = 16.sp,
                    color = if (isSelected) Color.White else Color.Black
                )
                Text(
                    text = pair.second,
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}
