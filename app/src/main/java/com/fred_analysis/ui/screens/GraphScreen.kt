package com.example.fred_analysis.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fred_analysis.viewmodel.GraphViewModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.Line
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController


@Composable
fun GraphScreen(navController : NavController,
                graphViewModel : GraphViewModel = hiltViewModel()
)
{
    Spacer(Modifier.height(100.dp))

    LaunchedEffect(Unit)
    {
        graphViewModel.loadData()
    }

    val graphState = graphViewModel.graphUiState.collectAsState()
    val points = graphState.value.observations.map {it.value}
    Log.d("graph", "Points: $points")
    val yValues = points.mapNotNull { it.toDoubleOrNull() }
    if (yValues.isNotEmpty()) {

        LineChart(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            data = listOf(
                Line(
                    label = "Data", values = yValues, color = Brush.verticalGradient(
                        colors = listOf(
                            Color.Blue, Color.Cyan
                        )
                    )
                )
            )

        )
    }
    else
    {
        Text("No Graph Available.")
    }





}
