package com.example.fred_analysis.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel(){


    private val _uiStateFlow : MutableStateFlow<uiState> = MutableStateFlow(uiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    data class uiState(val seriesId : String ="",
                       val startDate : String ="",
                       val endDate : String ="",

    )
    fun onSeriesIdChanged(id : String)
    {
        _uiStateFlow.update { it.copy(seriesId = id)}
    }
    fun onStartDateChanged(start : String)
    {
        _uiStateFlow.update { it.copy(startDate = start)}
    }
    fun onEndDateChanged(end : String)
    {
        _uiStateFlow.update { it.copy(endDate = end)}
    }



}