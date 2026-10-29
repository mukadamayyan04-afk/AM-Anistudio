package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AudioStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val scenes by viewModel.scenes.collectAsState()
    val characters by viewModel.characters.collectAsState()
    val ttsStatus by viewModel.ttsManager.statusMessage.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()

    var showSrtPreview by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080C14))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Headphones, contentDescription = null, tint = Color(0xFFFF007A), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "VOICE & AUDIO STUDIO",
                            color = Color(0xFFFF007A),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    val capabilityRouting by viewModel.capabilityRouting.collectAsState()
                    val voiceProvId = capabilityRouting["VOICE"] ?: "elevenlabs"
                    val voiceProvMeta = com.example.data.ai.ProviderDirectory.getMetadata(voiceProvId)
                    Text(
                        text = "VOICE ENGINE: ${voiceProvMeta?.name ?: voiceProvId.uppercase()} • $ttsStatus",
                        color = if (isSpeaking) Color(0xFF00F0FF) else Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable { viewModel.navigateTo(com.example.ui.viewmodel.StudioScreen.API_CONTROL_CENTER) }
                    )
                }

                OutlinedButton(
                    onClick = { showSrtPreview = !showSrtPreview },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Subtitles, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (showSrtPreview) "Hide SRT" else "SRT Subtitles", fontSize = 11.sp)
                }
            }
        }

        // Subtitle SRT Viewer Block
        if (showSrtPreview) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF060910), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFFFD166), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "EXPORT SUBTITLES (.SRT)",
                            color = Color(0xFFFFD166),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        var timeAcc = 0f
                        val srtBuilder = StringBuilder()
                        scenes.forEachIndexed { i, s ->
                            if (s.dialogue.isNotBlank()) {
                                val startMins = (timeAcc / 60).toInt()
                                val startSecs = (timeAcc % 60).toInt()
                                val endAcc = timeAcc + s.durationSeconds
                                val endMins = (endAcc / 60).toInt()
                                val endSecs = (endAcc % 60).toInt()

                                srtBuilder.append("${i + 1}\n")
                                srtBuilder.append(String.format("00:%02d:%02d,000 --> 00:%02d:%02d,000\n", startMins, startSecs, endMins, endSecs))
                                srtBuilder.append("${s.speakerName}: ${s.dialogue}\n\n")
                            }
                            timeAcc += s.durationSeconds
                        }
                        Text(
                            text = srtBuilder.toString().ifEmpty { "No dialogue cues in project." },
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Cast Voice Profile Cards
        item {
            Text(
                text = "CAST VOICE PROFILES",
                color = Color(0xFF00F0FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        items(characters) { char ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0E1524), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(char.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(4.dp)) {
                                Text(char.role, color = Color(0xFF00F0FF), fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Engine: Android Neural TTS • Pitch: ${String.format("%.2f", char.voicePitch)}x • Speed: ${String.format("%.2f", char.voiceSpeed)}x",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.ttsManager.speak(
                                "Voice test for ${char.name}. System operational.",
                                pitch = char.voicePitch,
                                speed = char.voiceSpeed
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007A)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Audition", fontSize = 10.sp)
                    }
                }
            }
        }

        // Dialogue Cue Sheet
        item {
            Text(
                text = "PRODUCTION DIALOGUE CUE SHEET",
                color = Color(0xFF00F0FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        items(scenes) { scene ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0E17), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Scene ${scene.sceneIndex + 1}: ${scene.title}",
                                color = Color(scene.accentColor),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (scene.dialogue.isNotBlank()) "\"${scene.dialogue}\"" else "(Silent shot)",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Speaker: ${scene.speakerName.ifEmpty { "Narrator" }} • BGM: ${scene.bgmTrack}",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }

                    if (scene.dialogue.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val char = characters.find { it.name == scene.speakerName }
                                viewModel.ttsManager.speak(
                                    scene.dialogue,
                                    pitch = char?.voicePitch ?: 1.0f,
                                    speed = char?.voiceSpeed ?: 1.0f
                                )
                            }
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play Dialogue", tint = Color(0xFF00F0FF))
                        }
                    }
                }
            }
        }
    }
}
