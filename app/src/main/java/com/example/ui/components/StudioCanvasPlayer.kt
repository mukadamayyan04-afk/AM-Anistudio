package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SceneEntity

@Composable
fun StudioCanvasPlayer(
    currentScene: SceneEntity?,
    isPlaying: Boolean,
    currentTimeSeconds: Float,
    totalDurationSeconds: Float,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onPreviousScene: () -> Unit,
    onNextScene: () -> Unit,
    onSpeakDialogue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "anime_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070A0F))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        // Player Viewport (16:9)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color(0xFF030508))
        ) {
            val sceneAccent = Color(currentScene?.accentColor ?: 0xFF00F0FF)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Dark Anime Sky & Neon City Horizon Gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF04060C),
                            Color(0xFF0A1224),
                            Color(0xFF160B24)
                        )
                    )
                )

                // Neon Grid Floor
                val horizonY = h * 0.65f
                for (i in -8..8) {
                    val startX = w / 2f + (i * w * 0.15f)
                    drawLine(
                        color = sceneAccent.copy(alpha = 0.25f),
                        start = Offset(w / 2f, horizonY),
                        end = Offset(startX, h),
                        strokeWidth = 1.5f
                    )
                }
                for (step in 1..6) {
                    val y = horizonY + (h - horizonY) * (step / 6f) * (step / 6f)
                    drawLine(
                        color = sceneAccent.copy(alpha = 0.3f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.5f
                    )
                }

                // Anime Moon / Cyber Core
                val moonRadius = h * 0.18f * (if (isPlaying) pulse else 1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sceneAccent.copy(alpha = 0.8f),
                            Color(0xFFFF007A).copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    radius = moonRadius * 1.6f,
                    center = Offset(w * 0.5f, h * 0.35f)
                )
                drawCircle(
                    color = sceneAccent,
                    radius = moonRadius * 0.65f,
                    center = Offset(w * 0.5f, h * 0.35f)
                )

                // Stylized Anime Character Silhouette in Center
                val charCenterX = w * 0.5f
                val charBottomY = horizonY + 30f
                val headRadius = h * 0.07f

                // Body Cloak Path
                val cloak = Path().apply {
                    moveTo(charCenterX, charBottomY - h * 0.32f)
                    lineTo(charCenterX + w * 0.08f, charBottomY)
                    lineTo(charCenterX - w * 0.08f, charBottomY)
                    close()
                }
                drawPath(cloak, color = Color(0xFF0A0F1A))

                // Head Silhouette
                drawCircle(
                    color = Color(0xFF0A0F1A),
                    radius = headRadius,
                    center = Offset(charCenterX, charBottomY - h * 0.33f)
                )

                // Glowing Cyan Visor Line
                drawLine(
                    color = Color(0xFF00F0FF),
                    start = Offset(charCenterX - headRadius * 0.6f, charBottomY - h * 0.33f),
                    end = Offset(charCenterX + headRadius * 0.6f, charBottomY - h * 0.33f),
                    strokeWidth = 3f
                )

                // Glowing Cyber Katana / Energy Blade
                val katanaPath = Path().apply {
                    moveTo(charCenterX + w * 0.06f, charBottomY - h * 0.45f)
                    lineTo(charCenterX + w * 0.065f, charBottomY - h * 0.1f)
                }
                drawLine(
                    color = Color(0xFFFF007A),
                    start = Offset(charCenterX + w * 0.06f, charBottomY - h * 0.45f),
                    end = Offset(charCenterX + w * 0.065f, charBottomY - h * 0.05f),
                    strokeWidth = 3.5f
                )

                // Holographic Scanline
                if (isPlaying) {
                    val lineY = h * scanY
                    drawLine(
                        color = Color(0x3300F0FF),
                        start = Offset(0f, lineY),
                        end = Offset(w, lineY),
                        strokeWidth = 2f
                    )
                }

                // Aspect Ratio Cinema Frame Borders
                drawRect(
                    color = Color.Black,
                    size = androidx.compose.ui.geometry.Size(w, h * 0.06f)
                )
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(0f, h * 0.94f),
                    size = androidx.compose.ui.geometry.Size(w, h * 0.06f)
                )
            }

            // Top Info HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xB30B111E),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "REC [24 FPS] • 1080p • ${currentScene?.cameraDirection ?: "Cinematic Pan"}",
                        color = Color(0xFF00F0FF),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = Color(0xB30B111E),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(if (isPlaying) Color(0xFF06D6A0) else Color(0xFFFF3366), CircleShape)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isPlaying) "PLAYING" else "PAUSED",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Subtitles Overlay at bottom of canvas
            if (!currentScene?.dialogue.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 18.dp)
                        .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${if (currentScene?.speakerName?.isNotEmpty() == true) "${currentScene.speakerName}: " else ""}\"${currentScene?.dialogue}\"",
                        color = Color(0xFFFFE600),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Scrub Bar & Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentMins = (currentTimeSeconds / 60).toInt()
                val currentSecs = (currentTimeSeconds % 60).toInt()
                val totalMins = (totalDurationSeconds / 60).toInt()
                val totalSecs = (totalDurationSeconds % 60).toInt()

                Text(
                    text = String.format("%02d:%02d / %02d:%02d", currentMins, currentSecs, totalMins, totalSecs),
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = currentScene?.title ?: "No Scene Selected",
                    color = Color(0xFF00F0FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Slider(
                value = currentTimeSeconds.coerceIn(0f, totalDurationSeconds.coerceAtLeast(1f)),
                onValueChange = onSeek,
                valueRange = 0f..totalDurationSeconds.coerceAtLeast(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00F0FF),
                    activeTrackColor = Color(0xFF00F0FF),
                    inactiveTrackColor = Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )

            // Playback buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPreviousScene, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Previous Scene", tint = Color.White)
                    }
                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF00F0FF), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = onNextScene, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.FastForward, contentDescription = "Next Scene", tint = Color.White)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSpeakDialogue,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Speak Dialogue",
                            tint = Color(0xFFFF007A)
                        )
                    }
                }
            }
        }
    }
}
