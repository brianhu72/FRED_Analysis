package com.example.fred_analysis.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fred_analysis.model.FredResponse
import com.example.fred_analysis.model.Observation
import com.example.fred_analysis.model.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GraphViewModel @Inject constructor(savedStateHandle : SavedStateHandle, private val retrofitInstance : RetrofitInstance) : ViewModel()
{
    val _graphUiState  = MutableStateFlow(FredResponse(observations = emptyList<Observation>()))
    val graphUiState = _graphUiState.asStateFlow()
    val seriesId : String= savedStateHandle["seriesId"] ?: ""
    val startDate : String= savedStateHandle["startDate"] ?: ""
    val endDate :String= savedStateHandle["endDate"] ?: ""

    fun loadData()
    {

        viewModelScope.launch(Dispatchers.IO)
        {
                val response = retrofitInstance.fredApiService.getObservations(
                    seriesId = seriesId,
                    startDate = startDate,
                    endDate = endDate
                )

                if (response.isSuccessful) {
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



