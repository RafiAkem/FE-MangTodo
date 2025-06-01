package com.example.fe_mangtodo.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    BottomAppBar(
        actions = {
            IconButton(onClick = onHomeClick) {
                Icon(Icons.Default.Home, contentDescription = "Home")
            }

            Spacer(modifier = Modifier.weight(1f)) // This allows FAB to stay centered

            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = "Profile")
            }
        }
    )
}
