package com.example.final_fred_display.viewmodel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_fred_display.model.FredResponse
import com.example.final_fred_display.model.Observation
import com.example.final_fred_display.model.RetrofitInstance
import com.example.final_fred_display.viewmodel.HomeViewModel.uiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GraphViewModel @Inject constructor(savedStateHandle : SavedStateHandle, private val retrofitInstance : RetrofitInstance) : ViewModel()
{
    val _graphUiState : MutableStateFlow<FredResponse> = MutableStateFlow(FredResponse(observations = emptyList<Observation>()))
    val graphUiState = _graphUiState.asStateFlow()
    val seriesId : String= savedStateHandle["seriesId"] ?: ""
    val startDate : String= savedStateHandle["startDate"] ?: ""
    val endDate :String= savedStateHandle["endDate"] ?: ""

    suspend fun loadData()
    {
        viewModelScope.launch()
        {

            val response = retrofitInstance.fredApiService.getObservations(
                    seriesId = seriesId,
                    startDate = startDate,
                    endDate = endDate
                )
            if (response.isSuccessful)
            {
                val fredResponse = response.body()
                Log.d("GraphScreen", "fredResponse: $fredResponse")
                if (!response.isSuccessful) {
                    Log.e("GraphScreen", "Request failed: ${response.code()}")
                }
                fredResponse?.let {
                    _graphUiState.value = it
                }
            }

        }


    }

}



