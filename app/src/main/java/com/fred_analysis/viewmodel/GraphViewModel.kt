package com.example.fred_analysis.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fred_analysis.model.Observation
import com.example.fred_analysis.model.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GraphViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val retrofitInstance: RetrofitInstance
) : ViewModel() {
    private val seriesIds = savedStateHandle.get<String>("seriesIds").orEmpty().split(',').filter { it.isNotBlank() }
    private val startDate = savedStateHandle.get<String>("startDate").orEmpty()
    private val endDate = savedStateHandle.get<String>("endDate").orEmpty()

    private val _uiState = MutableStateFlow(GraphUiState(seriesIds = seriesIds, dateRange = "$startDate to $endDate"))
    val uiState = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val results = seriesIds.map { id -> async { loadSeries(id) } }.awaitAll()
            val successful = results.filter { it.observations.isNotEmpty() }
            if (successful.isEmpty()) _uiState.update { it.copy(isLoading = false, errorMessage = "No observations were returned for the selected series.") }
            else _uiState.update { it.copy(isLoading = false, series = successful) }
        } catch (_: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Unable to reach FRED. Check your connection and try again.") }
        }
    }

    private suspend fun loadSeries(id: String): SeriesData {
        val response = retrofitInstance.fredApiService.getObservations(id, startDate = startDate, endDate = endDate)
        if (!response.isSuccessful) return SeriesData(id)
        return SeriesData(id, response.body()?.observations.orEmpty().filter { it.value.toDoubleOrNull() != null })
    }
}

data class GraphUiState(
    val seriesIds: List<String>,
    val dateRange: String,
    val series: List<SeriesData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class SeriesData(val id: String, val observations: List<Observation> = emptyList())
