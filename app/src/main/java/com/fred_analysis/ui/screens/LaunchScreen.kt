package com.example.fred_analysis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LaunchScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("FRED Analysis", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("Visualizing Economic Data", color = MaterialTheme.colorScheme.onPrimary)
        Spacer(Modifier.height(36.dp))
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
    }
}
