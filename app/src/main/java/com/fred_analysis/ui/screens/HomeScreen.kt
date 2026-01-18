package com.example.fred_analysis.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fred_analysis.viewmodel.HomeViewModel



@Composable
fun HomeScreen(homeViewModel : HomeViewModel = hiltViewModel(),
               navController : NavController
)
{
    val uiState = homeViewModel.uiStateFlow.collectAsState().value
    Spacer(modifier = Modifier
        .height(100.dp))
    Column(modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement= Arrangement.spacedBy(10.dp))
    {

        OutlinedTextField(
            value = uiState.seriesId,
            onValueChange = { value -> homeViewModel.onSeriesIdChanged(value)},
            label = { Text("Enter a series ID: ") },
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.startDate,
            onValueChange = { value -> homeViewModel.onStartDateChanged(value) },
            label = { Text("Enter a start date: (YYYY-MM-DD)" )},
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.endDate,
            onValueChange = { value -> homeViewModel.onEndDateChanged(value)},
            label = { Text("Enter an end date: (YYYY-MM-DD)") },
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        )
        var counter =0
        Button(onClick = {
            counter+=1
            navController.navigate("graph/${uiState.seriesId}/${uiState.startDate}/${uiState.endDate}/${counter}") })
        {
            Text("To the graph screen!")
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