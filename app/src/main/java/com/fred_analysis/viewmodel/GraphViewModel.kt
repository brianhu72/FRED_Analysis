package com.example.fred_analysis.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fred_analysis.BuildConfig
import com.example.fred_analysis.model.AnthropicMessage
import com.example.fred_analysis.model.AnthropicRequest
import com.example.fred_analysis.model.Observation
import com.example.fred_analysis.model.FredObservationCache
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
    private val retrofitInstance: RetrofitInstance,
    private val observationCache: FredObservationCache
) : ViewModel() {
    private val seriesIds = savedStateHandle.get<String>("seriesIds").orEmpty().split(',').filter { it.isNotBlank() }
    private val startDate = savedStateHandle.get<String>("startDate").orEmpty()
    private val endDate = savedStateHandle.get<String>("endDate").orEmpty()

    private val _uiState = MutableStateFlow(GraphUiState(seriesIds = seriesIds, dateRange = "$startDate to $endDate"))
    val uiState = _uiState.asStateFlow()

    init { loadData() }

    fun loadData(forceRefresh: Boolean = false) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, aiSummary = null, aiError = false) }
        try {
            val results = seriesIds.map { id -> async { loadSeries(id, forceRefresh) } }.awaitAll()
            val successful = results.filter { it.observations.isNotEmpty() }
            if (successful.isEmpty()) _uiState.update { it.copy(isLoading = false, errorMessage = "No observations were returned for the selected series.") }
            else {
                _uiState.update { it.copy(isLoading = false, series = successful) }
                generateAiSummary(successful)
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Unable to reach FRED. Check your connection and try again.") }
        }
    }

    fun refresh() = loadData(forceRefresh = true)

    private fun generateAiSummary(series: List<SeriesData>) = viewModelScope.launch {
        if (BuildConfig.ANTHROPIC_API_KEY.isBlank()) return@launch
        _uiState.update { it.copy(aiLoading = true, aiError = false) }
        try {
            val prompt = buildAiPrompt(series)
            val response = retrofitInstance.anthropicApiService.createMessage(
                AnthropicRequest(
                    model = "claude-opus-5",
                    maxTokens = 400,
                    system = "You are an economics assistant embedded in a data app. " +
                        "Given summary statistics for one or more FRED economic series, write a concise, " +
                        "plain-language read of what the numbers show in 2-3 sentences. When multiple series " +
                        "are present, note how their movements relate. Do not give financial or investment " +
                        "advice, do not use markdown, and do not repeat the raw numbers verbatim.",
                    messages = listOf(AnthropicMessage(role = "user", content = prompt))
                )
            )
            val text = response.body()?.content
                ?.firstOrNull { it.type == "text" }
                ?.text
                ?.trim()
            if (response.isSuccessful && !text.isNullOrEmpty()) {
                _uiState.update { it.copy(aiLoading = false, aiSummary = text) }
            } else {
                _uiState.update { it.copy(aiLoading = false, aiError = true) }
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(aiLoading = false, aiError = true) }
        }
    }

    private fun buildAiPrompt(series: List<SeriesData>): String {
        val header = "Date range: $startDate to $endDate.\n\n"
        val body = series.joinToString("\n\n") { s ->
            val values = s.observations.mapNotNull { it.value.toDoubleOrNull() }
            val first = values.firstOrNull()
            val latest = values.lastOrNull()
            val changePercent = if (first != null && latest != null && first != 0.0)
                (latest - first) / kotlin.math.abs(first) * 100 else null
            buildString {
                append("Series ${s.id}: ${values.size} observations. ")
                if (first != null && latest != null) append("Start ${formatValue(first)}, latest ${formatValue(latest)}. ")
                if (changePercent != null) append("Change ${formatPercent(changePercent)} over the period. ")
                if (values.isNotEmpty()) append("Range ${formatValue(values.min())} to ${formatValue(values.max())}, average ${formatValue(values.average())}.")
            }
        }
        return header + body
    }

    private suspend fun loadSeries(id: String, forceRefresh: Boolean): SeriesData {
        val cacheKey = FredObservationCache.CacheKey(id, startDate, endDate)
        val cachedObservations = if (forceRefresh) null else observationCache.get(cacheKey)
        if (cachedObservations != null) {
            return SeriesData(id, cachedObservations, buildInsight(id, cachedObservations.mapNotNull { it.value.toDoubleOrNull() }))
        }
        val response = retrofitInstance.fredApiService.getObservations(
            seriesId = id,
            apiKey = BuildConfig.FRED_API_KEY,
            startDate = startDate,
            endDate = endDate
        )
        if (!response.isSuccessful) return SeriesData(id)
        val observations = response.body()?.observations.orEmpty().filter { it.value.toDoubleOrNull() != null }
        observationCache.put(cacheKey, observations)
        return SeriesData(id, observations, buildInsight(id, observations.mapNotNull { it.value.toDoubleOrNull() }))
    }

    private fun buildInsight(id: String, values: List<Double>): SeriesInsight? {
        val first = values.firstOrNull() ?: return null
        val latest = values.lastOrNull() ?: return null
        val change = latest - first
        val changePercent = if (first == 0.0) null else (change / kotlin.math.abs(first)) * 100
        val direction = if (change >= 0) "increased" else "decreased"
        val headline = if (changePercent == null) {
            "$id $direction by ${formatValue(kotlin.math.abs(change))} across ${values.size} observations."
        } else {
            "$id $direction by ${formatPercent(kotlin.math.abs(changePercent))} across ${values.size} observations."
        }
        val average = values.average()
        val recentValues = values.takeLast(minOf(4, values.size))
        val recentDirection = if (recentValues.last() >= recentValues.first()) "upward" else "downward"
        return SeriesInsight(
            headline = headline,
            details = listOf(
                "Latest value: ${formatValue(latest)}; period average: ${formatValue(average)}.",
                "Range: ${formatValue(values.min())} to ${formatValue(values.max())}.",
                "Recent movement is $recentDirection."
            )
        )
    }

    private fun formatValue(value: Double): String = "%,.2f".format(value)
    private fun formatPercent(value: Double): String = "%.1f%%".format(value)
}

data class GraphUiState(
    val seriesIds: List<String>,
    val dateRange: String,
    val series: List<SeriesData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val aiLoading: Boolean = false,
    val aiSummary: String? = null,
    val aiError: Boolean = false
)

data class SeriesData(
    val id: String,
    val observations: List<Observation> = emptyList(),
    val insight: SeriesInsight? = null
)

data class SeriesInsight(val headline: String, val details: List<String>)
