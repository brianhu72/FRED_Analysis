package com.example.final_fred_display.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.final_fred_display.viewmodel.GraphViewModel
import com.example.final_fred_display.viewmodel.HomeViewModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.Line
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


@Composable
fun GraphScreen(navHostController: NavHostController,
                graphViewModel : GraphViewModel = hiltViewModel()
)
{
    Spacer(Modifier.height(50.dp))
    Text("FRED GRAPH", fontWeight = FontWeight.Bold, fontSize=20.sp,textAlign = TextAlign.Center)
    Spacer(Modifier.height(20.dp))
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
