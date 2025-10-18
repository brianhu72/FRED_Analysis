package com.example.final_fred_display.model
import com.example.final_fred_display.viewmodel.HomeViewModel
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FREDApiService {
    @GET("fred/series/observations")
    suspend fun getObservations(
        @Query ("series_id") seriesId : String,
        @Query ("api_key") apiKey : String ="588b07a6e209bb7121e8e8c874f8a680",
        @Query ("realtime_start") startDate : String,
        @Query ("realtime_end") endDate : String,
        @Query ("file_type") fileType : String = "json"
    ) : Response<FredResponse>

}
data class Observation(val realtime_start : String, val realtime_end: String, val date : String, val value : String )
data class FredResponse(
    @SerializedName("observations") val observations : List<Observation>
)