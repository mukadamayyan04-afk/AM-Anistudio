package com.example.data.ai

import com.example.BuildConfig
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

enum class ConnectionStatus {
    CONNECTED,
    INVALID_KEY,
    NOT_CONFIGURED,
    RATE_LIMITED,
    ERROR
}

data class ProviderTestResult(
    val providerId: String,
    val status: ConnectionStatus,
    val latencyMs: Long,
    val summary: String,
    val rawStatusCode: Int,
    val modelCount: Int = 0,
    val resolvedModelId: String = ""
)

class ProviderIntegrationService {

    fun getEffectiveKey(providerId: String, userKey: String?): String {
        val trimmed = userKey?.trim()
        if (!trimmed.isNullOrEmpty()) return trimmed

        // Check if Gemini is configured in BuildConfig
        if (providerId == "gemini") {
            return try {
                BuildConfig.GEMINI_API_KEY.trim()
            } catch (e: Throwable) {
                ""
            }
        }
        return ""
    }

    suspend fun testConnection(
        providerId: String,
        rawKey: String?,
        modelId: String
    ): ProviderTestResult = withContext(Dispatchers.IO) {
        val key = getEffectiveKey(providerId, rawKey)
        if (key.isEmpty()) {
            return@withContext ProviderTestResult(
                providerId = providerId,
                status = ConnectionStatus.NOT_CONFIGURED,
                latencyMs = 0,
                summary = "Not Configured: Please enter an API key for ${ProviderDirectory.getMetadata(providerId)?.name ?: providerId}.",
                rawStatusCode = 0,
                resolvedModelId = modelId
            )
        }

        val startTime = System.currentTimeMillis()
        try {
            when (providerId) {
                "gemini" -> testGemini(key, modelId, startTime)
                "openai" -> testOpenAi(key, modelId, startTime)
                "claude" -> testAnthropic(key, modelId, startTime)
                "openrouter" -> testOpenRouter(key, modelId, startTime)
                "groq" -> testGroq(key, modelId, startTime)
                "huggingface" -> testHuggingFace(key, modelId, startTime)
                "replicate" -> testReplicate(key, modelId, startTime)
                "fal_ai" -> testFalAi(key, modelId, startTime)
                "stability_ai" -> testStabilityAi(key, modelId, startTime)
                "runway" -> testRunway(key, modelId, startTime)
                "elevenlabs" -> testElevenLabs(key, modelId, startTime)
                "tripo_ai" -> testTripoAi(key, modelId, startTime)
                "meshy_ai" -> testMeshyAi(key, modelId, startTime)
                else -> ProviderTestResult(
                    providerId = providerId,
                    status = ConnectionStatus.ERROR,
                    latencyMs = 0,
                    summary = "Unknown provider: $providerId",
                    rawStatusCode = 400
                )
            }
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            ProviderTestResult(
                providerId = providerId,
                status = ConnectionStatus.ERROR,
                latencyMs = latency,
                summary = "Network failure: ${cleanErrorMessage(e.localizedMessage ?: "Connection error")}",
                rawStatusCode = 0,
                resolvedModelId = modelId
            )
        }
    }

    private fun testGemini(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models?key=$key"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val count = JSONObject(body).optJSONArray("models")?.length() ?: 0
            ProviderTestResult(
                providerId = "gemini",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Google AI API verified ($count models available)",
                rawStatusCode = code,
                modelCount = count,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("gemini", code, errBody, latency, modelId)
        }
    }

    private fun testOpenAi(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.openai.com/v1/models"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val count = JSONObject(body).optJSONArray("data")?.length() ?: 0
            ProviderTestResult(
                providerId = "openai",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: OpenAI platform verified ($count models accessible)",
                rawStatusCode = code,
                modelCount = count,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("openai", code, errBody, latency, modelId)
        }
    }

    private fun testAnthropic(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.anthropic.com/v1/messages"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("x-api-key", key)
            setRequestProperty("anthropic-version", "2023-06-01")
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
            connectTimeout = 10000
            readTimeout = 10000
        }
        val payload = JSONObject().apply {
            put("model", if (modelId.isNotBlank()) modelId else "claude-3-5-haiku-20241022")
            put("max_tokens", 1)
            put("messages", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("content", "ping")
            }))
        }
        OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()); it.flush() }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            ProviderTestResult(
                providerId = "claude",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Anthropic Claude API verified",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("claude", code, errBody, latency, modelId)
        }
    }

    private fun testOpenRouter(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://openrouter.ai/api/v1/auth/key"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val label = JSONObject(body).optJSONObject("data")?.optString("label") ?: "Valid Key"
            ProviderTestResult(
                providerId = "openrouter",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: OpenRouter authenticated ($label)",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("openrouter", code, errBody, latency, modelId)
        }
    }

    private fun testGroq(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.groq.com/openai/v1/models"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val count = JSONObject(body).optJSONArray("data")?.length() ?: 0
            ProviderTestResult(
                providerId = "groq",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Groq LPU engine verified ($count models available)",
                rawStatusCode = code,
                modelCount = count,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("groq", code, errBody, latency, modelId)
        }
    }

    private fun testHuggingFace(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://huggingface.co/api/whoami-v2"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val name = JSONObject(body).optString("name", "User")
            ProviderTestResult(
                providerId = "huggingface",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Hugging Face user '$name' authenticated",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("huggingface", code, errBody, latency, modelId)
        }
    }

    private fun testReplicate(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.replicate.com/v1/account"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val user = JSONObject(body).optString("username", "Account")
            ProviderTestResult(
                providerId = "replicate",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Replicate account '$user' verified",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("replicate", code, errBody, latency, modelId)
        }
    }

    private fun testFalAi(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://rest.alpha.fal.ai/tokens"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            val authValue = if (key.startsWith("Key ")) key else "Key $key"
            setRequestProperty("Authorization", authValue)
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            ProviderTestResult(
                providerId = "fal_ai",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: fal.ai API key verified",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("fal_ai", code, errBody, latency, modelId)
        }
    }

    private fun testStabilityAi(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.stability.ai/v1/user/account"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val email = JSONObject(body).optString("email", "Active")
            ProviderTestResult(
                providerId = "stability_ai",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Stability AI account verified ($email)",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("stability_ai", code, errBody, latency, modelId)
        }
    }

    private fun testRunway(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.dev.runwayml.com/v1/tasks"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            setRequestProperty("X-Runway-Version", "2024-09-13")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            ProviderTestResult(
                providerId = "runway",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Runway Gen-3 API active",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("runway", code, errBody, latency, modelId)
        }
    }

    private fun testElevenLabs(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.elevenlabs.io/v1/user"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("xi-api-key", key)
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val sub = JSONObject(body).optJSONObject("subscription")
            val tier = sub?.optString("tier") ?: "Active"
            val count = sub?.optInt("character_count") ?: 0
            val limit = sub?.optInt("character_limit") ?: 0
            ProviderTestResult(
                providerId = "elevenlabs",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: ElevenLabs Tier '$tier' ($count / $limit chars)",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("elevenlabs", code, errBody, latency, modelId)
        }
    }

    private fun testTripoAi(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.tripo3d.ai/v2/openapi/user/balance"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val balance = JSONObject(body).optJSONObject("data")?.optString("balance") ?: "Available"
            ProviderTestResult(
                providerId = "tripo_ai",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Tripo 3D Studio active (Balance: $balance)",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("tripo_ai", code, errBody, latency, modelId)
        }
    }

    private fun testMeshyAi(key: String, modelId: String, startTime: Long): ProviderTestResult {
        val endpoint = "https://api.meshy.ai/openapi/v1/credits"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Authorization", "Bearer $key")
            connectTimeout = 10000
            readTimeout = 10000
        }
        val code = conn.responseCode
        val latency = System.currentTimeMillis() - startTime
        return if (code in 200..299) {
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val credits = JSONObject(body).optInt("credits", 0)
            ProviderTestResult(
                providerId = "meshy_ai",
                status = ConnectionStatus.CONNECTED,
                latencyMs = latency,
                summary = "Connected: Meshy 3D API active (Credits: $credits)",
                rawStatusCode = code,
                resolvedModelId = modelId
            )
        } else {
            val errBody = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            parseErrorResponse("meshy_ai", code, errBody, latency, modelId)
        }
    }

    private fun parseErrorResponse(
        providerId: String,
        code: Int,
        errBody: String,
        latency: Long,
        modelId: String
    ): ProviderTestResult {
        val cleanMsg = cleanErrorMessage(errBody)
        val status = when (code) {
            401, 403 -> ConnectionStatus.INVALID_KEY
            429 -> ConnectionStatus.RATE_LIMITED
            else -> ConnectionStatus.ERROR
        }
        val prefix = when (status) {
            ConnectionStatus.INVALID_KEY -> "Invalid Key (HTTP $code)"
            ConnectionStatus.RATE_LIMITED -> "Rate Limited / Quota Depleted (HTTP $code)"
            else -> "Provider Error (HTTP $code)"
        }
        return ProviderTestResult(
            providerId = providerId,
            status = status,
            latencyMs = latency,
            summary = "$prefix: $cleanMsg",
            rawStatusCode = code,
            resolvedModelId = modelId
        )
    }

    private fun cleanErrorMessage(raw: String): String {
        return try {
            val obj = JSONObject(raw)
            when {
                obj.has("error") -> {
                    val err = obj.get("error")
                    if (err is JSONObject) {
                        err.optString("message", raw)
                    } else err.toString()
                }
                obj.has("message") -> obj.getString("message")
                obj.has("detail") -> obj.getString("detail")
                else -> raw.take(120)
            }
        } catch (e: Exception) {
            raw.take(120).replace("\n", " ").trim()
        }
    }
}
