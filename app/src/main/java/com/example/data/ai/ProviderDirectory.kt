package com.example.data.ai

enum class AICapability(val label: String, val badgeColor: Long) {
    TEXT("Text & Scripts", 0xFF00F0FF),
    IMAGE("Keyframes & 2D", 0xFFFF007A),
    VIDEO("Video & Animation", 0xFFFFD166),
    ASSETS_3D("3D Meshes", 0xFF9D4EDD),
    VOICE("Voice & Speech", 0xFF06D6A0)
}

data class ProviderMetadata(
    val id: String,
    val name: String,
    val company: String,
    val description: String,
    val capabilities: List<AICapability>,
    val verifiedModels: List<String>,
    val defaultModel: String,
    val endpointUrl: String,
    val documentationUrl: String,
    val billingNotice: String,
    val keyPlaceholder: String,
    val keyPrefixHelp: String
)

object ProviderDirectory {

    fun maskKey(key: String): String {
        val trimmed = key.trim()
        if (trimmed.isEmpty()) return ""
        return if (trimmed.length > 8) {
            val start = trimmed.take(4)
            val end = trimmed.takeLast(4)
            "$start••••••••$end"
        } else {
            "••••••••••••"
        }
    }

    val ALL_PROVIDERS: List<ProviderMetadata> = listOf(
        ProviderMetadata(
            id = "gemini",
            name = "Google Gemini",
            company = "Google",
            description = "Multimodal reasoning, script breakdown, anime character profiles, scene direction, and audio orchestration.",
            capabilities = listOf(AICapability.TEXT, AICapability.IMAGE, AICapability.VOICE),
            verifiedModels = listOf("gemini-3.8-flash", "gemini-2.5-flash", "gemini-1.5-flash", "gemini-3.8-flash-tts"),
            defaultModel = "gemini-3.8-flash",
            endpointUrl = "https://generativelanguage.googleapis.com/v1beta/models",
            documentationUrl = "https://ai.google.dev/gemini-api/docs",
            billingNotice = "Generous free tier with Google AI Studio. Rate limits apply to unbilled tiers.",
            keyPlaceholder = "AIzaSy...",
            keyPrefixHelp = "Begins with AIza..."
        ),
        ProviderMetadata(
            id = "openai",
            name = "OpenAI",
            company = "OpenAI",
            description = "GPT-4o reasoning, DALL-E 3 keyframe concept generation, and neural TTS voice narration.",
            capabilities = listOf(AICapability.TEXT, AICapability.IMAGE, AICapability.VOICE),
            verifiedModels = listOf("gpt-4o", "gpt-4o-mini", "dall-e-3", "tts-1", "tts-1-hd"),
            defaultModel = "gpt-4o",
            endpointUrl = "https://api.openai.com/v1",
            documentationUrl = "https://platform.openai.com/docs",
            billingNotice = "Pay-as-you-go billing required. Verify funded credits in OpenAI platform.",
            keyPlaceholder = "sk-proj-...",
            keyPrefixHelp = "Begins with sk-..."
        ),
        ProviderMetadata(
            id = "claude",
            name = "Anthropic Claude",
            company = "Anthropic",
            description = "Long-context anime narrative arcs, deep character continuity, and nuanced episodic dialogue.",
            capabilities = listOf(AICapability.TEXT),
            verifiedModels = listOf("claude-3-7-sonnet-20250219", "claude-3-5-sonnet-20241022", "claude-3-5-haiku-20241022"),
            defaultModel = "claude-3-5-sonnet-20241022",
            endpointUrl = "https://api.anthropic.com/v1",
            documentationUrl = "https://docs.anthropic.com",
            billingNotice = "Credit balance required in Anthropic Console before making calls.",
            keyPlaceholder = "sk-ant-...",
            keyPrefixHelp = "Begins with sk-ant-..."
        ),
        ProviderMetadata(
            id = "openrouter",
            name = "OpenRouter",
            company = "OpenRouter",
            description = "Unified multi-model gateway connecting Llama 3.3, DeepSeek, Claude, and open-source models with fallback routing.",
            capabilities = listOf(AICapability.TEXT, AICapability.IMAGE),
            verifiedModels = listOf("meta-llama/llama-3.3-70b-instruct", "anthropic/claude-3.5-sonnet", "deepseek/deepseek-chat", "google/gemini-2.0-flash-001"),
            defaultModel = "meta-llama/llama-3.3-70b-instruct",
            endpointUrl = "https://openrouter.ai/api/v1",
            documentationUrl = "https://openrouter.ai/docs",
            billingNotice = "Pre-fund credit wallet on OpenRouter.ai.",
            keyPlaceholder = "sk-or-v1-...",
            keyPrefixHelp = "Begins with sk-or-..."
        ),
        ProviderMetadata(
            id = "groq",
            name = "Groq",
            company = "Groq LPU",
            description = "Ultra-fast low-latency LPU inference for real-time script revisions, dialogue iteration, and story breakdowns.",
            capabilities = listOf(AICapability.TEXT),
            verifiedModels = listOf("llama-3.3-70b-versatile", "llama-3.1-8b-instant", "mixtral-8x7b-32768"),
            defaultModel = "llama-3.3-70b-versatile",
            endpointUrl = "https://api.groq.com/openai/v1",
            documentationUrl = "https://console.groq.com/docs",
            billingNotice = "Free developer tier available with requests-per-minute (RPM) limits.",
            keyPlaceholder = "gsk_...",
            keyPrefixHelp = "Begins with gsk_..."
        ),
        ProviderMetadata(
            id = "huggingface",
            name = "Hugging Face",
            company = "Hugging Face",
            description = "Open access to FLUX.1, SDXL, and thousands of community anime LoRAs and checkpoints.",
            capabilities = listOf(AICapability.IMAGE, AICapability.TEXT),
            verifiedModels = listOf("black-forest-labs/FLUX.1-schnell", "stabilityai/stable-diffusion-xl-base-1.0", "meta-llama/Llama-3.2-3B-Instruct"),
            defaultModel = "black-forest-labs/FLUX.1-schnell",
            endpointUrl = "https://api-inference.huggingface.co",
            documentationUrl = "https://huggingface.co/docs/api-inference",
            billingNotice = "Free inference quota per month for standard models; PRO subscription for zero cold-starts.",
            keyPlaceholder = "hf_...",
            keyPrefixHelp = "Begins with hf_..."
        ),
        ProviderMetadata(
            id = "replicate",
            name = "Replicate",
            company = "Replicate",
            description = "Cloud GPU deployment for FLUX, AnimateDiff, Stable Video Diffusion, and anime upscalers.",
            capabilities = listOf(AICapability.IMAGE, AICapability.VIDEO),
            verifiedModels = listOf("black-forest-labs/flux-schnell", "stability-ai/sdxl", "lucataco/animate-diff"),
            defaultModel = "black-forest-labs/flux-schnell",
            endpointUrl = "https://api.replicate.com/v1",
            documentationUrl = "https://replicate.com/docs",
            billingNotice = "Billed per second of GPU hardware execution time.",
            keyPlaceholder = "r8_...",
            keyPrefixHelp = "Begins with r8_..."
        ),
        ProviderMetadata(
            id = "fal_ai",
            name = "fal.ai",
            company = "fal",
            description = "Ultra-fast media compute engine for FLUX Schnell, Fast SVD, and real-time video generation pipelines.",
            capabilities = listOf(AICapability.IMAGE, AICapability.VIDEO),
            verifiedModels = listOf("fal-ai/flux/schnell", "fal-ai/fast-svd/text-to-video", "fal-ai/minimax/video"),
            defaultModel = "fal-ai/flux/schnell",
            endpointUrl = "https://rest.alpha.fal.ai",
            documentationUrl = "https://fal.ai/docs",
            billingNotice = "Pay-per-request billing with initial developer balance.",
            keyPlaceholder = "Key ...",
            keyPrefixHelp = "API key or Key format"
        ),
        ProviderMetadata(
            id = "stability_ai",
            name = "Stability AI",
            company = "Stability AI",
            description = "SD3 Large, Stable Video Diffusion, and specialized anime style rendering.",
            capabilities = listOf(AICapability.IMAGE, AICapability.VIDEO),
            verifiedModels = listOf("sd3-large", "stable-diffusion-xl-1024-v1-0", "stable-video-diffusion"),
            defaultModel = "sd3-large",
            endpointUrl = "https://api.stability.ai/v1",
            documentationUrl = "https://platform.stability.ai/docs",
            billingNotice = "Requires purchasing credit packs on Stability Platform.",
            keyPlaceholder = "sk-...",
            keyPrefixHelp = "Begins with sk-..."
        ),
        ProviderMetadata(
            id = "runway",
            name = "Runway",
            company = "RunwayML",
            description = "Cinematic video generation with Gen-3 Alpha for anime camera movement and shot continuation.",
            capabilities = listOf(AICapability.VIDEO),
            verifiedModels = listOf("gen-3-alpha", "gen-2"),
            defaultModel = "gen-3-alpha",
            endpointUrl = "https://api.dev.runwayml.com/v1",
            documentationUrl = "https://docs.runwayml.com",
            billingNotice = "Runway developer account with active credit balance required.",
            keyPlaceholder = "key_...",
            keyPrefixHelp = "Begins with key_..."
        ),
        ProviderMetadata(
            id = "elevenlabs",
            name = "ElevenLabs",
            company = "ElevenLabs",
            description = "Studio-grade anime character voice acting, multilingual emotional voice synthesis, and sound effect generation.",
            capabilities = listOf(AICapability.VOICE),
            verifiedModels = listOf("eleven_multilingual_v2", "eleven_flash_v2_5", "eleven_turbo_v2_5"),
            defaultModel = "eleven_multilingual_v2",
            endpointUrl = "https://api.elevenlabs.io/v1",
            documentationUrl = "https://elevenlabs.io/docs",
            billingNotice = "Free tier provides 10,000 characters per month. Character count resets monthly.",
            keyPlaceholder = "xi-api-key...",
            keyPrefixHelp = "32-character hexadecimal key"
        ),
        ProviderMetadata(
            id = "tripo_ai",
            name = "Tripo AI",
            company = "VAST / Tripo",
            description = "Text/Image to 3D anime mesh generation, auto-retopologized GLB models, and character geometry.",
            capabilities = listOf(AICapability.ASSETS_3D),
            verifiedModels = listOf("tripo-v2.5", "tripo-v2.0"),
            defaultModel = "tripo-v2.5",
            endpointUrl = "https://api.tripo3d.ai/v2/openapi",
            documentationUrl = "https://platform.tripo3d.ai/docs",
            billingNotice = "Requires Tripo Studio credits. Free registration trial credits available.",
            keyPlaceholder = "tsk_...",
            keyPrefixHelp = "Begins with tsk_..."
        ),
        ProviderMetadata(
            id = "meshy_ai",
            name = "Meshy AI",
            company = "Meshy",
            description = "Generative 3D anime character models, PBR textures, cel-shaded materials, and rigging presets.",
            capabilities = listOf(AICapability.ASSETS_3D),
            verifiedModels = listOf("meshy-4", "meshy-3"),
            defaultModel = "meshy-4",
            endpointUrl = "https://api.meshy.ai/openapi/v1",
            documentationUrl = "https://docs.meshy.ai",
            billingNotice = "Free tier provides 200 monthly credits. Pro subscription for commercial exports.",
            keyPlaceholder = "msy_...",
            keyPrefixHelp = "Begins with msy_..."
        )
    )

    fun getMetadata(id: String): ProviderMetadata? {
        return ALL_PROVIDERS.find { it.id == id }
    }

    fun findById(id: String): ProviderMetadata? = getMetadata(id)

    fun findByCapability(capability: AICapability): List<ProviderMetadata> {
        return ALL_PROVIDERS.filter { it.capabilities.contains(capability) }
    }
}

val ProviderMetadata.models: List<String> get() = verifiedModels
val ProviderMetadata.docsUrl: String get() = documentationUrl
val ProviderMetadata.billingWarning: String get() = billingNotice

