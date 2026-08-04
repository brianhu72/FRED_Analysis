package com.example.fred_analysis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fred_analysis.model.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    private val formState = MutableStateFlow(HomeUiState())
    val uiState = combine(formState, favoritesRepository.favorites) { state, favorites ->
        state.copy(favorites = favorites.toList().sorted())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onSeriesIdChanged(value: String) = update { copy(seriesId = value.uppercase(), formError = null) }
    fun onStartDateChanged(value: String) = update { copy(startDate = value, formError = null) }
    fun onEndDateChanged(value: String) = update { copy(endDate = value, formError = null) }
    fun toggleFavorite(seriesId: String = formState.value.seriesId) { if (seriesId.isNotBlank()) favoritesRepository.toggle(seriesId) }

    fun addCurrentSeries() {
        val state = formState.value
        validate(state)?.let { error -> formState.update { it.copy(formError = error) }; return }
        if (state.comparisonSeries.contains(state.seriesId)) return
        if (state.comparisonSeries.size == MAX_COMPARISON_SERIES) {
            formState.update { it.copy(formError = "You can compare up to $MAX_COMPARISON_SERIES series at once.") }
        } else update { copy(comparisonSeries = comparisonSeries + seriesId, formError = null) }
    }

    fun removeFromComparison(seriesId: String) = update { copy(comparisonSeries = comparisonSeries - seriesId) }
    fun useFavorite(seriesId: String) = update { copy(seriesId = seriesId, formError = null) }

    fun submit(onValid: (List<String>, String, String) -> Unit) {
        val state = formState.value
        validate(state)?.let { error -> formState.update { it.copy(formError = error) }; return }
        val series = (state.comparisonSeries + state.seriesId).distinct()
        onValid(series, state.startDate, state.endDate)
    }

    private fun validate(state: HomeUiState): String? = when {
        state.seriesId.isBlank() -> "Enter a FRED series ID to continue."
        !isDate(state.startDate) || !isDate(state.endDate) -> "Use dates in YYYY-MM-DD format."
        state.startDate > state.endDate -> "The start date must be before the end date."
        else -> null
    }

    private fun update(block: HomeUiState.() -> HomeUiState) = formState.update(block)
    private fun isDate(value: String) = runCatching { LocalDate.parse(value) }.isSuccess
    private companion object { const val MAX_COMPARISON_SERIES = 4 }
}

data class HomeUiState(
    val seriesId: String = "GDP",
    val startDate: String = "2019-01-01",
    val endDate: String = LocalDate.now().toString(),
    val comparisonSeries: List<String> = emptyList(),
    val favorites: List<String> = emptyList(),
    val formError: String? = null
)
