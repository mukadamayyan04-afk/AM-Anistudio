package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.CharacterEntity
import com.example.data.model.SceneEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

data class ConnectionTestResult(
    val isSuccess: Boolean,
    val latencyMs: Long,
    val message: String,
    val resolvedModelId: String,
    val httpStatusCode: Int = 0
)

data class StoryOutlineResult(
    val title: String,
    val logline: String,
    val theme: String,
    val actsSummary: String,
    val visualStyleNotes: String
)

class GeminiApiService {

    fun getEffectiveApiKey(customKey: String? = null): String {
        val trimmedCustom = customKey?.trim()
        if (!trimmedCustom.isNullOrEmpty()) return trimmedCustom
        return try {
            BuildConfig.GEMINI_API_KEY.trim()
        } catch (e: Throwable) {
            ""
        }
    }

    suspend fun testConnection(
        customKey: String? = null,
        modelId: String = "gemini-3.8-flash"
    ): ConnectionTestResult = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(customKey)
        if (apiKey.isEmpty()) {
            return@withContext ConnectionTestResult(
                isSuccess = false,
                latencyMs = 0,
                message = "API key not found. Please provide your Gemini API key in Provider Manager or AI Studio Secrets.",
                resolvedModelId = modelId,
                httpStatusCode = 401
            )
        }

        val startTime = System.currentTimeMillis()
        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$modelId:generateContent?key=$apiKey"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 12000
                readTimeout = 12000
            }

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", "Ping. Reply with 'AM AniStudio Online'.")
                    }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("maxOutputTokens", 20)
                })
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestJson.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            val latency = System.currentTimeMillis() - startTime

            if (responseCode in 200..299) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(responseText)
                val candidates = jsonObj.optJSONArray("candidates")
                val reply = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: "Connected"

                ConnectionTestResult(
                    isSuccess = true,
                    latencyMs = latency,
                    message = "Active & verified: ${reply.trim()} (${latency}ms)",
                    resolvedModelId = modelId,
                    httpStatusCode = responseCode
                )
            } else {
                val errorStream = connection.errorStream
                val errorBody = errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                val errorMsg = try {
                    JSONObject(errorBody).optJSONObject("error")?.optString("message") ?: errorBody
                } catch (e: Exception) {
                    errorBody
                }
                ConnectionTestResult(
                    isSuccess = false,
                    latencyMs = latency,
                    message = "API Error ($responseCode): $errorMsg",
                    resolvedModelId = modelId,
                    httpStatusCode = responseCode
                )
            }
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            ConnectionTestResult(
                isSuccess = false,
                latencyMs = latency,
                message = "Network / Request failure: ${e.localizedMessage ?: "Unknown error"}",
                resolvedModelId = modelId,
                httpStatusCode = 0
            )
        }
    }

    suspend fun generateStoryOutline(
        prompt: String,
        genre: String,
        artStyle: String,
        targetDurationMinutes: Int,
        customKey: String? = null,
        modelId: String = "gemini-3.8-flash"
    ): Result<StoryOutlineResult> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(customKey)
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is required. Please set it in AI Studio Secrets or the AI Provider Manager.")
            )
        }

        val systemInstruction = """
            You are the Lead Anime Director & Showrunner at AM AniStudio.
            Generate a cinematic anime story outline based on the user's premise.
            Return STRICTLY valid JSON with no markdown wrapping or markdown code blocks:
            {
               "title": "Anime title",
               "logline": "1-2 sentence compelling logline",
               "theme": "Core anime thematic conflict",
               "actsSummary": "Act I Setup, Act II Escalation, Act III Climax",
               "visualStyleNotes": "Specific anime aesthetic, lighting, color palette, keyframe notes"
            }
        """.trimIndent()

        val promptBody = "Genre: $genre\nArt Style: $artStyle\nTarget Duration: $targetDurationMinutes mins\nStory Concept: $prompt"

        try {
            val rawOutput = callGeminiRaw(apiKey, modelId, systemInstruction, promptBody)
            val cleanJson = cleanJsonString(rawOutput)
            val json = JSONObject(cleanJson)
            val result = StoryOutlineResult(
                title = json.optString("title", "Project Untitled"),
                logline = json.optString("logline", prompt),
                theme = json.optString("theme", "Destiny vs Free Will"),
                actsSummary = json.optString("actsSummary", "Act 1: Awakening. Act 2: Cyber Confrontation. Act 3: Final Resolution."),
                visualStyleNotes = json.optString("visualStyleNotes", "$artStyle with volumetric lighting and high contrast anime shadows.")
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateCharacters(
        storyContext: String,
        projectId: String,
        customKey: String? = null,
        modelId: String = "gemini-3.8-flash"
    ): Result<List<CharacterEntity>> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(customKey)
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(IllegalStateException("Gemini API key is missing."))
        }

        val systemInstruction = """
            You are an anime character designer at AM AniStudio.
            Generate 3 distinct anime characters for this story.
            Return STRICTLY a JSON array with no markdown ticks:
            [
              {
                "name": "Character Name",
                "role": "Protagonist / Antagonist / Supporting / Mentor",
                "personality": "Short personality traits and motivation",
                "voiceName": "Voice persona name (e.g. Kira, Ren, Sora, Aiko)",
                "visualPrompt": "Detailed visual appearance prompt for keyframe generation, hair, eyes, cyberware, clothing",
                "model3DType": "CYBER_WARRIOR or CYBER_MECHA or ANIME_CHIBI or MAGICAL_GIRL",
                "outfitVariant": "Battle Suit / Stealth Cloak / Academy Uniform",
                "expressionNotes": "Fierce, smirking, focused"
              }
            ]
        """.trimIndent()

        try {
            val rawOutput = callGeminiRaw(apiKey, modelId, systemInstruction, storyContext)
            val cleanJson = cleanJsonString(rawOutput)
            val array = JSONArray(cleanJson)
            val characters = mutableListOf<CharacterEntity>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                characters.add(
                    CharacterEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        name = obj.optString("name", "Character ${i + 1}"),
                        role = obj.optString("role", if (i == 0) "Protagonist" else "Supporting"),
                        personality = obj.optString("personality", "Determined warrior"),
                        voiceName = obj.optString("voiceName", "Kira"),
                        visualPrompt = obj.optString("visualPrompt", "Anime character, vibrant glowing details, cinematic shot"),
                        model3DType = obj.optString("model3DType", "CYBER_WARRIOR"),
                        outfitVariant = obj.optString("outfitVariant", "Standard Gear"),
                        expressionNotes = obj.optString("expressionNotes", "Focused expression")
                    )
                )
            }
            Result.success(characters)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateScenes(
        storyContext: String,
        characters: List<CharacterEntity>,
        projectId: String,
        sceneCount: Int = 4,
        customKey: String? = null,
        modelId: String = "gemini-3.8-flash"
    ): Result<List<SceneEntity>> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(customKey)
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(IllegalStateException("Gemini API key is missing."))
        }

        val charList = characters.joinToString(", ") { "${it.name} (${it.role})" }
        val systemInstruction = """
            You are an anime storyboard director at AM AniStudio.
            Generate $sceneCount sequential anime scene breakdowns for this production.
            Available characters: $charList.
            Return STRICTLY a JSON array with no markdown ticks:
            [
              {
                "title": "Scene Heading",
                "visualPrompt": "Detailed visual prompt for anime rendering: environment, lighting, character pose, visual effects",
                "speakerName": "Speaking character name or Narrator",
                "dialogue": "Line spoken in English or Roman Hinglish",
                "cameraDirection": "e.g. Extreme Close-up on Cyber Katana / Low-angle Tracking Shot / Sweeping Aerial Crane",
                "durationSeconds": 5,
                "bgmTrack": "Synthwave Combat / Orchestral Suspense / Melancholic Rain",
                "soundEffect": "Plasma blade hiss / Thunder clap / Footsteps on wet asphalt"
              }
            ]
        """.trimIndent()

        try {
            val rawOutput = callGeminiRaw(apiKey, modelId, systemInstruction, storyContext)
            val cleanJson = cleanJsonString(rawOutput)
            val array = JSONArray(cleanJson)
            val scenes = mutableListOf<SceneEntity>()
            val colors = listOf(0xFF00F0FF, 0xFFFF007A, 0xFF9D4EDD, 0xFFFFD166, 0xFF06D6A0)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                scenes.add(
                    SceneEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        sceneIndex = i,
                        title = obj.optString("title", "Scene ${i + 1}"),
                        visualPrompt = obj.optString("visualPrompt", "Cinematic anime shot with dynamic lighting"),
                        speakerName = obj.optString("speakerName", characters.firstOrNull()?.name ?: "Narrator"),
                        dialogue = obj.optString("dialogue", "The battle has only begun..."),
                        narratorText = obj.optString("narratorText", ""),
                        cameraDirection = obj.optString("cameraDirection", "Cinematic Medium Pan"),
                        durationSeconds = obj.optInt("durationSeconds", 5).coerceIn(3, 30),
                        bgmTrack = obj.optString("bgmTrack", "Cyber Strike"),
                        soundEffect = obj.optString("soundEffect", "Energy Surge"),
                        accentColor = colors[i % colors.size]
                    )
                )
            }
            Result.success(scenes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun callGeminiRaw(
        apiKey: String,
        modelId: String,
        systemInstruction: String,
        userContent: String
    ): String {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$modelId:generateContent?key=$apiKey"
        val url = URL(endpoint)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
            connectTimeout = 20000
            readTimeout = 30000
        }

        val requestPayload = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().apply {
                    put("text", systemInstruction)
                }))
            })
            put("contents", JSONArray().put(JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().apply {
                    put("text", userContent)
                }))
            }))
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("maxOutputTokens", 2048)
            })
        }

        OutputStreamWriter(conn.outputStream).use { it.write(requestPayload.toString()); it.flush() }

        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $code"
            throw IllegalStateException("Gemini API error ($code): $err")
        }

        val res = conn.inputStream.bufferedReader().use { it.readText() }
        val root = JSONObject(res)
        val candidates = root.optJSONArray("candidates") ?: throw IllegalStateException("No response candidate returned")
        if (candidates.length() == 0) throw IllegalStateException("Empty candidates in response")
        val content = candidates.getJSONObject(0).optJSONObject("content") ?: throw IllegalStateException("No content in candidate")
        val parts = content.optJSONArray("parts") ?: throw IllegalStateException("No parts in candidate")
        return parts.getJSONObject(0).optString("text")
    }

    private fun cleanJsonString(raw: String): String {
        var str = raw.trim()
        if (str.startsWith("```json")) {
            str = str.removePrefix("```json").trim()
        } else if (str.startsWith("```")) {
            str = str.removePrefix("```").trim()
        }
        if (str.endsWith("```")) {
            str = str.removeSuffix("```").trim()
        }
        return str
    }
}
