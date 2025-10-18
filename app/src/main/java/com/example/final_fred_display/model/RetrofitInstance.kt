package com.example.final_fred_display.model
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class RetrofitInstance @Inject constructor()
{
    private val okhttpClient = OkHttpClient.Builder().build()
    val fredApiService : FREDApiService by lazy()
    {
        Retrofit.Builder()
            .baseUrl("https://api.stlouisfed.org/")
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FREDApiService::class.java)


    }
}