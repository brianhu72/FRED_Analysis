package com.example.final_fred_display.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.final_fred_display.viewmodel.HomeViewModel



@Composable
fun HomeScreen(homeViewModel : HomeViewModel = hiltViewModel(),
               navHostController : NavHostController
)
{
    val uiState = homeViewModel.uiStateFlow.collectAsState().value
    Surface(color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 100.dp, horizontal=30.dp)
    )
    {
        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement= Arrangement.spacedBy(16.dp))
        {
            Text(
                "FRED Display",
                fontSize = 20.sp,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = uiState.seriesId,
                onValueChange = { value -> homeViewModel.onSeriesIdChanged(value)},
                label = { Text("Enter a series ID: ") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.startDate,
                onValueChange = { value -> homeViewModel.onStartDateChanged(value) },
                label = { Text("Enter a start date: (YYYY-MM-DD)" )},
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.endDate,
                onValueChange = { value -> homeViewModel.onEndDateChanged(value)},
                label = { Text("Enter an end date: (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                navHostController.navigate("graph/${uiState.seriesId}/${uiState.startDate}/${uiState.endDate}") })
            {
                Text("To the graph screen!")
            }
        }
    }
}
/*
@Preview
@Composable
fun HomeScreenPreview()
{
    val fakeHomeViewModel : HomeViewModel=hiltViewModel()
    HomeScreen(homeViewModel = fakeHomeViewModel)
}

 */