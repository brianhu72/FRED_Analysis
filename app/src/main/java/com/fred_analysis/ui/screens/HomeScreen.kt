package com.example.fred_analysis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fred_analysis.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onViewChart: (List<String>, String, String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val state by homeViewModel.uiState.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("Explore a series", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Choose a FRED series and date range to see its history and recent movement.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SeriesForm(
                seriesId = state.seriesId,
                startDate = state.startDate,
                endDate = state.endDate,
                comparisonSeries = state.comparisonSeries,
                favorites = state.favorites,
                error = state.formError,
                onSeriesChanged = homeViewModel::onSeriesIdChanged,
                onStartChanged = homeViewModel::onStartDateChanged,
                onEndChanged = homeViewModel::onEndDateChanged,
                onAddCurrent = homeViewModel::addCurrentSeries,
                onRemoveSeries = homeViewModel::removeFromComparison,
                onToggleFavorite = homeViewModel::toggleFavorite,
                onUseFavorite = homeViewModel::useFavorite,
                onSubmit = { homeViewModel.submit(onViewChart) }
            )
            ReferenceCard()
        }
    }
}

@Composable
private fun SeriesForm(
    seriesId: String,
    startDate: String,
    endDate: String,
    comparisonSeries: List<String>,
    favorites: List<String>,
    error: String?,
    onSeriesChanged: (String) -> Unit,
    onStartChanged: (String) -> Unit,
    onEndChanged: (String) -> Unit,
    onAddCurrent: () -> Unit,
    onRemoveSeries: (String) -> Unit,
    onToggleFavorite: () -> Unit,
    onUseFavorite: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Series details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = seriesId,
                    onValueChange = onSeriesChanged,
                    modifier = Modifier.weight(1f),
                    label = { Text("Series ID") },
                    supportingText = { Text("For example: GDP, UNRATE, CPIAUCSL") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                )
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        if (favorites.contains(seriesId)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        if (favorites.contains(seriesId)) "Remove from favorites" else "Add to favorites",
                        tint = if (favorites.contains(seriesId)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = startDate, onValueChange = onStartChanged, modifier = Modifier.weight(1f),
                    label = { Text("From") }, singleLine = true, leadingIcon = { Icon(Icons.Default.CalendarToday, null) }
                )
                OutlinedTextField(
                    value = endDate, onValueChange = onEndChanged, modifier = Modifier.weight(1f),
                    label = { Text("To") }, singleLine = true, leadingIcon = { Icon(Icons.Default.CalendarToday, null) }
                )
            }
            if (favorites.isNotEmpty()) {
                Text("Favorites", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    favorites.take(4).forEach { favorite ->
                        AssistChip(onClick = { onUseFavorite(favorite) }, label = { Text(favorite) }, leadingIcon = { Icon(Icons.Default.Favorite, null, modifier = Modifier.width(16.dp)) })
                    }
                }
            }
            if (comparisonSeries.isNotEmpty()) {
                Text("Comparing ${comparisonSeries.size} of 4", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    comparisonSeries.forEach { id ->
                        AssistChip(
                            onClick = { onRemoveSeries(id) },
                            label = { Text(id) },
                            trailingIcon = { Icon(Icons.Default.Close, "Remove $id", modifier = Modifier.width(16.dp)) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                }
            }
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onAddCurrent, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(6.dp))
                Text("Add current series to comparison")
            }
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (comparisonSeries.isEmpty()) "View chart" else "Compare ${comparisonSeries.size + 1} series", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
    }
}

@Composable
private fun ReferenceCard() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.QueryStats, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Data is provided directly by the Federal Reserve Economic Data API.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
