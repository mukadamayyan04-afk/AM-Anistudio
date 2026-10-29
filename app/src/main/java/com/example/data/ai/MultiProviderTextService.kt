package com.example.data.ai

import com.example.data.model.CharacterEntity
import com.example.data.model.SceneEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class MultiProviderTextService(
    private val geminiService: GeminiApiService,
    private val integrationService: ProviderIntegrationService
) {

    suspend fun generateStoryOutline(
        providerId: String,
        apiKey: String?,
        modelId: String,
        prompt: String,
        genre: String,
        artStyle: String,
        targetDurationMinutes: Int
    ): Result<StoryOutlineResult> = withContext(Dispatchers.IO) {
        val effectiveKey = integrationService.getEffectiveKey(providerId, apiKey)

        // Route to Gemini
        if (providerId == "gemini") {
            return@withContext geminiService.generateStoryOutline(
                prompt = prompt,
                genre = genre,
                artStyle = artStyle,
                targetDurationMinutes = targetDurationMinutes,
                customKey = effectiveKey,
                modelId = if (modelId.isNotBlank()) modelId else "gemini-3.8-flash"
            )
        }

        if (effectiveKey.isEmpty()) {
            return@withContext Result.failure(
                IllegalStateException("Selected provider '${ProviderDirectory.getMetadata(providerId)?.name ?: providerId}' is not configured with an API key. Please configure it in the API Control Center.")
            )
        }

        val systemInstruction = "You are the Lead Anime Director at AM AniStudio. Return STRICTLY a JSON object with keys: title, logline, theme, actsSummary, visualStyleNotes. No markdown ticks."
        val userPrompt = "Genre: $genre\nArt Style: $artStyle\nDuration: $targetDurationMinutes mins\nPremise: $prompt"

        try {
            val responseText = when (providerId) {
                "openai" -> callOpenAiCompatible(
                    endpoint = "https://api.openai.com/v1/chat/completions",
                    key = effectiveKey,
                    model = if (modelId.isNotBlank()) modelId else "gpt-4o",
                    system = systemInstruction,
                    user = userPrompt
                )
                "groq" -> callOpenAiCompatible(
                    endpoint = "https://api.groq.com/openai/v1/chat/completions",
                    key = effectiveKey,
                    model = if (modelId.isNotBlank()) modelId else "llama-3.3-70b-versatile",
                    system = systemInstruction,
                    user = userPrompt
                )
                "openrouter" -> callOpenAiCompatible(
                    endpoint = "https://openrouter.ai/api/v1/chat/completions",
                    key = effectiveKey,
                    model = if (modelId.isNotBlank()) modelId else "meta-llama/llama-3.3-70b-instruct",
                    system = systemInstruction,
                    user = userPrompt
                )
                "claude" -> callAnthropic(
                    key = effectiveKey,
                    model = if (modelId.isNotBlank()) modelId else "claude-3-5-sonnet-20241022",
                    system = systemInstruction,
                    user = userPrompt
                )
                else -> throw IllegalArgumentException("Provider $providerId does not support text generation.")
            }

            val clean = cleanJson(responseText)
            val json = JSONObject(clean)
            val result = StoryOutlineResult(
                title = json.optString("title", "Generated Anime Production"),
                logline = json.optString("logline", prompt),
                theme = json.optString("theme", "Conflict of Ideals"),
                actsSummary = json.optString("actsSummary", "Act I: Setup. Act II: Climax. Act III: Resolution."),
                visualStyleNotes = json.optString("visualStyleNotes", "$artStyle with volumetric keyframe lighting.")
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun callOpenAiCompatible(
        endpoint: String,
        key: String,
        model: String,
        system: String,
        user: String
    ): String {
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Authorization", "Bearer $key")
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
            connectTimeout = 20000
            readTimeout = 25000
        }

        val body = JSONObject().apply {
            put("model", model)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", system)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", user)
                })
            })
            put("temperature", 0.7)
        }

        OutputStreamWriter(conn.outputStream).use { it.write(body.toString()); it.flush() }
        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $code"
            throw IllegalStateException("API error ($code): ${err.take(120)}")
        }

        val res = conn.inputStream.bufferedReader().use { it.readText() }
        val choices = JSONObject(res).optJSONArray("choices") ?: throw IllegalStateException("Empty choices returned")
        return choices.getJSONObject(0).getJSONObject("message").getString("content")
    }

    private fun callAnthropic(key: String, model: String, system: String, user: String): String {
        val endpoint = "https://api.anthropic.com/v1/messages"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("x-api-key", key)
            setRequestProperty("anthropic-version", "2023-06-01")
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
            connectTimeout = 20000
            readTimeout = 25000
        }

        val body = JSONObject().apply {
            put("model", model)
            put("max_tokens", 1500)
            put("system", system)
            put("messages", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("content", user)
            }))
        }

        OutputStreamWriter(conn.outputStream).use { it.write(body.toString()); it.flush() }
        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $code"
            throw IllegalStateException("Anthropic API error ($code): ${err.take(120)}")
        }

        val res = conn.inputStream.bufferedReader().use { it.readText() }
        val content = JSONObject(res).optJSONArray("content") ?: throw IllegalStateException("Empty content array")
        return content.getJSONObject(0).getString("text")
    }

    private fun cleanJson(raw: String): String {
        var str = raw.trim()
        if (str.startsWith("```json")) str = str.removePrefix("```json").trim()
        if (str.startsWith("```")) str = str.removePrefix("```").trim()
        if (str.endsWith("```")) str = str.removeSuffix("```").trim()
        return str
    }
}
