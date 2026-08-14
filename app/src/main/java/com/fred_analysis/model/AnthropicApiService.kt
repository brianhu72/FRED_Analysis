package com.example.fred_analysis.model

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
interface AnthropicApiService {
    @POST("v1/messages")
    suspend fun createMessage(@Body request: AnthropicRequest): Response<AnthropicResponse>
}

data class AnthropicRequest(
    val model: String,
    @SerializedName("max_tokens") val maxTokens: Int,
    val system: String,
    val messages: List<AnthropicMessage>
)

data class AnthropicMessage(
    val role: String,
    val content: String
)

data class AnthropicResponse(
    val content: List<AnthropicContentBlock> = emptyList(),
    @SerializedName("stop_reason") val stopReason: String? = null
)

data class AnthropicContentBlock(
    val type: String,
    val text: String? = null
)
