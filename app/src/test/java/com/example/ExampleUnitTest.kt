package com.example

import com.example.data.model.CharacterEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SceneEntity
import com.example.ui.components.Vec3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testVec3MathOperations() {
        val v1 = Vec3(1f, 0f, 0f)
        val v2 = Vec3(0f, 1f, 0f)
        val cross = v1.cross(v2)
        assertEquals(0f, cross.x, 0.001f)
        assertEquals(0f, cross.y, 0.001f)
        assertEquals(1f, cross.z, 0.001f)

        val dot = v1.dot(v2)
        assertEquals(0f, dot, 0.001f)

        val norm = Vec3(0f, 3f, 4f).normalize()
        assertEquals(0.6f, norm.y, 0.001f)
        assertEquals(0.8f, norm.z, 0.001f)
    }

    @Test
    fun testProjectAndSceneEntityIntegrity() {
        val project = ProjectEntity(
            title = "NEO PROTOCOL",
            description = "Cyberpunk anime battle",
            artStyle = "Cyberpunk 2077 Anime"
        )
        assertNotNull(project.id)
        assertEquals(24, project.frameRate)

        val scene = SceneEntity(
            projectId = project.id,
            sceneIndex = 0,
            title = "Clash on the Highway",
            visualPrompt = "Speeding cyber-cycles on skyway",
            dialogue = "Accelerate to maximum!",
            durationSeconds = 6
        )
        assertEquals(6, scene.durationSeconds)
        assertEquals("PLANNED", scene.status)
    }

    @Test
    fun testCharacterArchetypeSetup() {
        val hero = CharacterEntity(
            projectId = "test_proj",
            name = "Kira",
            model3DType = "CYBER_WARRIOR"
        )
        assertEquals("CYBER_WARRIOR", hero.model3DType)
        assertTrue(hero.voicePitch > 0f)
    }

    @Test
    fun testProviderDirectoryCatalogHasAll13Providers() {
        val requiredIds = listOf(
            "gemini",
            "openai",
            "claude",
            "openrouter",
            "groq",
            "huggingface",
            "replicate",
            "fal_ai",
            "stability_ai",
            "runway",
            "elevenlabs",
            "tripo_ai",
            "meshy_ai"
        )

        assertEquals(13, com.example.data.ai.ProviderDirectory.ALL_PROVIDERS.size)

        for (id in requiredIds) {
            val provider = com.example.data.ai.ProviderDirectory.findById(id)
            assertNotNull("Provider $id must exist in catalog", provider)
            assertTrue("Provider $id must have non-empty name", provider!!.name.isNotBlank())
            assertTrue("Provider $id must have non-empty documentationUrl", provider.documentationUrl.startsWith("http"))
            assertTrue("Provider $id must support at least one capability", provider.capabilities.isNotEmpty())
            assertTrue("Provider $id must declare supported models", provider.verifiedModels.isNotEmpty())
        }
    }

    @Test
    fun testProviderKeyMaskingSecurity() {
        // Empty
        assertEquals("", com.example.data.ai.ProviderDirectory.maskKey(""))
        assertEquals("", com.example.data.ai.ProviderDirectory.maskKey("   "))

        // Short key (<= 8 chars)
        val shortMasked = com.example.data.ai.ProviderDirectory.maskKey("secret")
        assertEquals("••••••••••••", shortMasked)

        // Standard OpenAI style key
        val openAiKey = "sk-proj-1234567890abcdef1234"
        val masked = com.example.data.ai.ProviderDirectory.maskKey(openAiKey)
        assertTrue("Masked key must start with prefix", masked.startsWith("sk-"))
        assertTrue("Masked key must contain bullets", masked.contains("••••"))
        assertTrue("Masked key must end with last 4 chars", masked.endsWith("1234"))
        assertTrue("Raw middle key must not be exposed", !masked.contains("1234567890abcdef"))
    }

    @Test
    fun testCapabilityCoverageAcrossProviders() {
        val textProviders = com.example.data.ai.ProviderDirectory.findByCapability(com.example.data.ai.AICapability.TEXT)
        assertTrue("Must have multiple text providers", textProviders.size >= 4)
        assertTrue("Gemini must be in text providers", textProviders.any { it.id == "gemini" })
        assertTrue("OpenAI must be in text providers", textProviders.any { it.id == "openai" })
        assertTrue("Claude must be in text providers", textProviders.any { it.id == "claude" })

        val imageProviders = com.example.data.ai.ProviderDirectory.findByCapability(com.example.data.ai.AICapability.IMAGE)
        assertTrue("Must have multiple image providers", imageProviders.size >= 3)

        val videoProviders = com.example.data.ai.ProviderDirectory.findByCapability(com.example.data.ai.AICapability.VIDEO)
        assertTrue("Must have video providers", videoProviders.any { it.id == "runway" })

        val threeDProviders = com.example.data.ai.ProviderDirectory.findByCapability(com.example.data.ai.AICapability.ASSETS_3D)
        assertTrue("Must have 3D providers", threeDProviders.any { it.id == "tripo_ai" || it.id == "meshy_ai" })

        val voiceProviders = com.example.data.ai.ProviderDirectory.findByCapability(com.example.data.ai.AICapability.VOICE)
        assertTrue("Must have ElevenLabs for voice", voiceProviders.any { it.id == "elevenlabs" })
    }

    @Test
    fun testProviderConnectionStatusEnum() {
        val statuses = com.example.data.ai.ConnectionStatus.values()
        assertTrue(statuses.contains(com.example.data.ai.ConnectionStatus.NOT_CONFIGURED))
        assertTrue(statuses.contains(com.example.data.ai.ConnectionStatus.CONNECTED))
        assertTrue(statuses.contains(com.example.data.ai.ConnectionStatus.INVALID_KEY))
        assertTrue(statuses.contains(com.example.data.ai.ConnectionStatus.RATE_LIMITED))
        assertTrue(statuses.contains(com.example.data.ai.ConnectionStatus.ERROR))

        val result = com.example.data.ai.ProviderTestResult(
            providerId = "openai",
            status = com.example.data.ai.ConnectionStatus.CONNECTED,
            latencyMs = 142L,
            summary = "Connection established (models verified)",
            rawStatusCode = 200
        )
        assertEquals(142L, result.latencyMs)
        assertEquals(com.example.data.ai.ConnectionStatus.CONNECTED, result.status)
        assertEquals(200, result.rawStatusCode)
    }
}
