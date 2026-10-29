package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SceneEntity

@Composable
fun TimelineTrackView(
    scenes: List<SceneEntity>,
    selectedSceneId: String?,
    currentTimeSeconds: Float,
    totalDurationSeconds: Float,
    onSelectScene: (SceneEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val secondsToWidth = 24.dp // 1 second = 24 dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0E17), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        // Timeline Header with Track Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PRODUCTION MULTI-TRACK // ${scenes.size} SCENES",
                color = Color(0xFF00F0FF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${totalDurationSeconds.toInt()}s TOTAL",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.height(8.dp))

        // Tracks Container
        Row(modifier = Modifier.fillMaxWidth()) {
            // Track Header Labels (Sticky Left Column)
            Column(
                modifier = Modifier
                    .width(76.dp)
                    .padding(end = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Timecode space
                Box(modifier = Modifier.height(20.dp), contentAlignment = Alignment.CenterStart) {
                    Text("TIME", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                // Video Track Label
                Row(
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF131D2E), RoundedCornerShape(4.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.height(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("VIDEO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
                // Voice Track Label
                Row(
                    modifier = Modifier
                        .height(38.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF231429), RoundedCornerShape(4.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFFFF007A), modifier = Modifier.height(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("VOICE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
                // Audio/BGM Track Label
                Row(
                    modifier = Modifier
                        .height(34.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF19172B), RoundedCornerShape(4.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFF9D4EDD), modifier = Modifier.height(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("BGM", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
                // Subtitles Track Label
                Row(
                    modifier = Modifier
                        .height(28.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF1E1D13), RoundedCornerShape(4.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Subtitles, contentDescription = null, tint = Color(0xFFFFD166), modifier = Modifier.height(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("SUBS", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Scrollable Timeline Tracks
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Time Ruler
                    Row(
                        modifier = Modifier
                            .height(20.dp)
                            .background(Color(0xFF060910), RoundedCornerShape(2.dp))
                    ) {
                        for (s in 0..totalDurationSeconds.toInt() step 2) {
                            Box(
                                modifier = Modifier
                                    .width(secondsToWidth * 2)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Text(
                                    text = "${s}s",
                                    color = Color(0xFF64748B),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        }
                    }

                    // Video Clips Row
                    Row(
                        modifier = Modifier.height(52.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        scenes.forEach { scene ->
                            val isSelected = scene.id == selectedSceneId
                            val clipWidth = secondsToWidth * scene.durationSeconds.toFloat()
                            val clipColor = Color(scene.accentColor)

                            Box(
                                modifier = Modifier
                                    .width(clipWidth.coerceAtLeast(60.dp))
                                    .fillMaxHeight()
                                    .background(
                                        if (isSelected) clipColor.copy(alpha = 0.35f) else Color(0xFF151D2C),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) clipColor else Color(0xFF28364F),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { onSelectScene(scene) }
                                    .padding(6.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        text = scene.title,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${scene.durationSeconds}s",
                                            color = Color(0xFF00F0FF),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = scene.status,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 8.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Voice / Dialogue Track Row
                    Row(
                        modifier = Modifier.height(38.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        scenes.forEach { scene ->
                            val clipWidth = secondsToWidth * scene.durationSeconds.toFloat()
                            Box(
                                modifier = Modifier
                                    .width(clipWidth.coerceAtLeast(60.dp))
                                    .fillMaxHeight()
                                    .background(Color(0xFF2A1526), RoundedCornerShape(4.dp))
                                    .border(1.dp, Color(0xFF4A1F3E), RoundedCornerShape(4.dp))
                                    .padding(4.dp)
                            ) {
                                Column {
                                    Text(
                                        text = scene.speakerName.ifEmpty { "Narrator" },
                                        color = Color(0xFFFF007A),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = scene.dialogue.ifEmpty { "(No dialogue)" },
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 8.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // BGM / SFX Track Row
                    Row(
                        modifier = Modifier.height(34.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        scenes.forEach { scene ->
                            val clipWidth = secondsToWidth * scene.durationSeconds.toFloat()
                            Box(
                                modifier = Modifier
                                    .width(clipWidth.coerceAtLeast(60.dp))
                                    .fillMaxHeight()
                                    .background(Color(0xFF1E1730), RoundedCornerShape(4.dp))
                                    .border(1.dp, Color(0xFF3B2B5C), RoundedCornerShape(4.dp))
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "${scene.bgmTrack} • ${scene.soundEffect}",
                                    color = Color(0xFFC084FC),
                                    fontSize = 8.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Subtitles Track Row
                    Row(
                        modifier = Modifier.height(28.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        scenes.forEach { scene ->
                            val clipWidth = secondsToWidth * scene.durationSeconds.toFloat()
                            Box(
                                modifier = Modifier
                                    .width(clipWidth.coerceAtLeast(60.dp))
                                    .fillMaxHeight()
                                    .background(Color(0xFF262310), RoundedCornerShape(4.dp))
                                    .border(1.dp, Color(0xFF47411B), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (scene.dialogue.isNotBlank()) "SUB: ${scene.dialogue}" else "-",
                                    color = Color(0xFFFFD166),
                                    fontSize = 8.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Playhead scrub needle overlay
                val playheadX = secondsToWidth * currentTimeSeconds
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    val x = playheadX.toPx()
                    drawLine(
                        color = Color(0xFFFF0055),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 2.5f
                    )
                    drawCircle(
                        color = Color(0xFFFF0055),
                        radius = 5f,
                        center = Offset(x, 10f)
                    )
                }
            }
        }
    }
}
