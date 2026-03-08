package com.dyshiuk.eventapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onLogoutClick: () -> Unit,
    onEventsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login successful")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Welcome to EventApp")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onEventsClick) {
            Text("Open events")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onLogoutClick) {
            Text("Logout")
        }
    }
}