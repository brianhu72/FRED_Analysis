package com.example.fred_analysis.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fred_analysis.viewmodel.GraphUiState
import com.example.fred_analysis.viewmodel.SeriesData
import com.example.fred_analysis.viewmodel.GraphViewModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun GraphScreen(onBack: () -> Unit, graphViewModel: GraphViewModel = hiltViewModel()) {
    val state by graphViewModel.uiState.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Text("Series detail", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = graphViewModel::loadData) { Icon(Icons.Default.Refresh, "Refresh") }
        }
        when {
            state.isLoading -> LoadingContent()
            state.errorMessage != null -> ErrorContent(state.errorMessage.orEmpty(), graphViewModel::loadData)
            state.series.isEmpty() -> EmptyContent()
            else -> ChartContent(state)
        }
    }
}

@Composable
private fun ChartContent(state: GraphUiState) {
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(if (state.series.size == 1) state.series.first().id else "Series comparison", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(state.dateRange, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(Modifier.padding(16.dp)) {
                Text(if (state.series.size == 1) "History" else "Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                LineChart(
                    modifier = Modifier.fillMaxWidth().height(270.dp),
                    data = state.series.mapIndexed { index, series ->
                        Line(
                            label = series.id,
                            values = series.observations.mapNotNull { it.value.toDoubleOrNull() },
                            color = Brush.verticalGradient(listOf(chartColors[index % chartColors.size], chartColors[index % chartColors.size].copy(alpha = 0.55f)))
                        )
                    }
                )
            }
        }
        Text("Latest observations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        state.series.forEachIndexed { index, series -> SeriesSummary(series, chartColors[index % chartColors.size]) }
        Text("Data is provided by FRED. Series may use different units; compare the direction of movement when units differ.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SeriesSummary(series: SeriesData, color: Color) {
    val values = series.observations.mapNotNull { it.value.toDoubleOrNull() }
    val latest = values.lastOrNull() ?: return
    val first = values.firstOrNull() ?: latest
    val change = latest - first
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, null, tint = color, modifier = Modifier.width(18.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(series.id, fontWeight = FontWeight.SemiBold)
                Text("${series.observations.size} observations", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatValue(latest), fontWeight = FontWeight.SemiBold)
                Text("${if (change >= 0) "+" else ""}${formatValue(change)}", style = MaterialTheme.typography.labelMedium, color = if (change >= 0) Color(0xFF26734D) else MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(Modifier.height(5.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LoadingContent() = Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
    CircularProgressIndicator()
    Spacer(Modifier.height(16.dp))
    Text("Loading series data…", style = MaterialTheme.typography.titleMedium)
    Text("Retrieving the latest observations from FRED", color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) = Column(Modifier.fillMaxSize().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
    Text("Something went wrong", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(20.dp))
    Button(onClick = onRetry) { Text("Try again") }
}

@Composable
private fun EmptyContent() = Column(Modifier.fillMaxSize().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
    Text("No observations found", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    Text("Try a different date range or series ID.", color = MaterialTheme.colorScheme.onSurfaceVariant)
}

private val chartColors = listOf(Color(0xFF155E75), Color(0xFF7C3AED), Color(0xFFB45309), Color(0xFFBE123C))
private fun formatValue(value: Double): String = "%,.2f".format(value)
