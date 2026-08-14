package com.example.fred_analysis.model
import com.example.fred_analysis.BuildConfig
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

    private val anthropicClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-api-key", BuildConfig.ANTHROPIC_API_KEY)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val anthropicApiService: AnthropicApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(anthropicClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnthropicApiService::class.java)
    }
}
