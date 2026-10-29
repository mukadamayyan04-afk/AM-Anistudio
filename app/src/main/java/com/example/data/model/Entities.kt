package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val artStyle: String = "Cyberpunk 2077 Anime",
    val language: String = "English",
    val aspectRatio: String = "16:9",
    val resolution: String = "1080p (FHD)",
    val frameRate: Int = 24,
    val targetDurationSeconds: Int = 180,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val role: String = "Protagonist", // Protagonist, Antagonist, Supporting, Mentor
    val personality: String = "Stoic yet fiercely loyal cyber-blade specialist",
    val voiceName: String = "Kira",
    val voicePitch: Float = 1.0f,
    val voiceSpeed: Float = 1.0f,
    val visualPrompt: String = "Anime warrior with glowing neon cybernetic katana, midnight black trenchcoat, high collar, cyan holographic visor, dynamic wind, cinematic anime keyframe",
    val model3DType: String = "CYBER_WARRIOR", // CYBER_WARRIOR, CYBER_MECHA, ANIME_CHIBI, MAGICAL_GIRL
    val outfitVariant: String = "Combat Gear",
    val expressionNotes: String = "Focused, fierce, occasional warm smirk"
)

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val episodeId: String = "",
    val sceneIndex: Int = 0,
    val title: String,
    val visualPrompt: String,
    val dialogue: String = "",
    val speakerName: String = "",
    val narratorText: String = "",
    val cameraDirection: String = "Medium Close-up, Slow Dutch Angle Pan",
    val durationSeconds: Int = 5,
    val status: String = "PLANNED", // PLANNED, SCRIPTED, ASSETS_READY, RENDERED
    val bgmTrack: String = "Cyber Syndicate - Synthwave",
    val soundEffect: String = "Katana Energy Surge",
    val accentColor: Long = 0xFF00F0FF
)

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val episodeNumber: Int = 1,
    val title: String,
    val synopsis: String,
    val status: String = "IN_PRODUCTION",
    val targetDurationMinutes: Int = 3
)

@Entity(tableName = "render_jobs")
data class RenderJobEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val title: String,
    val format: String = "MP4 (H.264)",
    val resolution: String = "1920x1080",
    val fps: Int = 24,
    val status: String = "QUEUED", // QUEUED, PROCESSING, COMPLETED, FAILED
    val progress: Int = 0,
    val outputUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val logText: String = "Queued in Studio pipeline..."
)

@Entity(tableName = "provider_configs")
data class AIProviderConfigEntity(
    @PrimaryKey val providerId: String,
    val name: String,
    val isEnabled: Boolean = true,
    val apiKey: String = "",
    val modelId: String = "",
    val endpointUrl: String = "",
    val isDefault: Boolean = false,
    val lastLatencyMs: Long = 0,
    val lastStatus: String = "Not Configured",
    val capabilities: String = "TEXT",
    val supportedModels: String = "",
    val requiresBillingWarning: String = "",
    val documentationUrl: String = "",
    val maskedKey: String = "",
    val lastVerifiedAt: Long = 0
)

@Entity(tableName = "capability_routing")
data class CapabilityRoutingEntity(
    @PrimaryKey val capability: String,
    val providerId: String
)
