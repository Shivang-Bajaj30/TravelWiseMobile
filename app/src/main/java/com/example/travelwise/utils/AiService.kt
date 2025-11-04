package com.example.travelwise.utils

import com.example.travelwise.BuildConfig
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class AiService {

    private val apiKey: String = BuildConfig.GEMINI_API_KEY

    private val gson = Gson()
    private val client: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(90, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // Minimal Gemini response model
    data class Part(val text: String?)
    data class Content(val parts: List<Part>?)
    data class Candidate(val content: Content?)
    data class GenResponse(val candidates: List<Candidate>?)

    suspend fun generateTripPlan(prompt: String): String {
        if (apiKey.isBlank()) return "API key missing. Set BuildConfig.GEMINI_API_KEY."
        if (apiKey.length < 30) return "API key looks invalid. Please paste a full Gemini key."

        val configuredModel = BuildConfig.GEMINI_MODEL
        val preferredModels = if (configuredModel.isNotBlank()) listOf(configuredModel) else listOf(
            "gemini-2.5-flash",
            "gemini-2.5-pro"
        )

        val payload = gson.toJson(
            mapOf(
                "contents" to listOf(
                    mapOf(
                        "parts" to listOf(mapOf("text" to prompt))
                    )
                ),
                "generationConfig" to mapOf("temperature" to 0.7)
            )
        )
        val body = payload.toRequestBody("application/json; charset=utf-8".toMediaType())

        var lastError: String? = null

        for (model in preferredModels) {
            val url = "https://generativelanguage.googleapis.com/v1/models/$model:generateContent?key=$apiKey"

            var attempt = 0
            val maxAttempts = 4
            var backoffMs = 1500L

            while (attempt < maxAttempts) {
                val request = Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "TravelWise/1.0 (Android)")
                    .post(body)
                    .build()

                try {
                    val resp: Response = client.newCall(request).execute()
                    try {
                        if (!resp.isSuccessful) {
                            val code = resp.code
                            if (code == 429 || code == 500 || code == 502 || code == 503) {
                                attempt++
                                if (attempt >= maxAttempts) {
                                    lastError = if (code == 429) {
                                        "Rate limited (429) on $model after retries."
                                    } else {
                                        "Gemini service error ($code) on $model after retries."
                                    }
                                    break
                                }
                                val retryAfterHeader = resp.headers["Retry-After"]
                                val retryAfterMs = retryAfterHeader?.toLongOrNull()?.times(1000)
                                val sleepMs = retryAfterMs ?: backoffMs
                                try { Thread.sleep(sleepMs) } catch (_: InterruptedException) {}
                                backoffMs = (backoffMs * 2).coerceAtMost(15000)
                                continue
                            }

                            val errBody = try { resp.body?.string() } catch (_: Exception) { null }
                            if (code == 404 || (code == 403 && (errBody?.contains("permissions") == true))) {
                                // Try next model
                                lastError = "Model $model not available: ${errBody ?: code.toString()}"
                                break
                            }
                            return when (code) {
                                400 -> "Bad request (400)."
                                401 -> "Invalid API key (401). Check your GEMINI_API_KEY."
                                403 -> "Access denied (403). ${errBody ?: ""}"
                                else -> "HTTP error $code. ${errBody ?: ""}"
                            }
                        }
                        val bodyStr = resp.body?.string() ?: return "Empty response body."
                        val parsed = gson.fromJson(bodyStr, GenResponse::class.java)
                        val parts = parsed.candidates?.firstOrNull()?.content?.parts
                        val text = parts?.joinToString(separator = "\n") { it.text.orEmpty() }?.trim()
                        return if (!text.isNullOrBlank()) text else "No candidates returned."
                    } finally {
                        resp.close()
                    }
                } catch (e: IOException) {
                    attempt++
                    if (attempt >= maxAttempts) {
                        lastError = "Network error on $model: ${e.message}"
                        break
                    }
                    try { Thread.sleep(backoffMs) } catch (_: InterruptedException) {}
                    backoffMs = (backoffMs * 2).coerceAtMost(15000)
                } catch (e: Exception) {
                    return "AI error: ${e.message ?: "Unknown error"}"
                }
            }
            // Try next preferred model if we got here without returning
        }

        return lastError ?: "Request failed after retries. Please try again."
    }
}


