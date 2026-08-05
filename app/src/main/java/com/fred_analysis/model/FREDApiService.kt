package com.example.fred_analysis.model

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FREDApiService {
    @GET("fred/series/observations")
    suspend fun getObservations(
        @Query("series_id") seriesId: String,
        @Query("api_key") apiKey: String,
        @Query("observation_start") startDate: String,
        @Query("observation_end") endDate: String,
        @Query("file_type") fileType: String = "json"
    ): Response<FredResponse>
}

data class Observation(
    val date: String,
    val value: String
)

data class FredResponse(
    @SerializedName("observations") val observations: List<Observation>
)
