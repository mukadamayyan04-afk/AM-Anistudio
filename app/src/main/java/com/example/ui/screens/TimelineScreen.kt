package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StudioCanvasPlayer
import com.example.ui.components.TimelineTrackView
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun TimelineScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val scenes by viewModel.scenes.collectAsState()
    val selectedScene by viewModel.selectedScene.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentTime by viewModel.currentTimeSeconds.collectAsState()
    val totalDuration = viewModel.getTotalDurationSeconds()

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
                        Icon(Icons.Default.ViewTimeline, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "PRODUCTION TIMELINE",
                            color = Color(0xFF00F0FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "MULTI-TRACK COMPOSITOR // 24 FPS",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = { viewModel.navigateTo(StudioScreen.RENDER_QUEUE) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD166), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Realtime Viewport & Controls
        item {
            StudioCanvasPlayer(
                currentScene = selectedScene,
                isPlaying = isPlaying,
                currentTimeSeconds = currentTime,
                totalDurationSeconds = totalDuration,
                onTogglePlay = { viewModel.togglePlay() },
                onSeek = { viewModel.seekTo(it) },
                onPreviousScene = { viewModel.previousScene() },
                onNextScene = { viewModel.nextScene() },
                onSpeakDialogue = { viewModel.speakCurrentDialogue() }
            )
        }

        // Multi-Track Timeline
        item {
            TimelineTrackView(
                scenes = scenes,
                selectedSceneId = selectedScene?.id,
                currentTimeSeconds = currentTime,
                totalDurationSeconds = totalDuration,
                onSelectScene = { viewModel.selectScene(it) }
            )
        }

        // Selected Clip Quick Inspector
        val current = selectedScene
        if (current != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE CLIP // ${current.title}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = Color(0x3300F0FF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${current.durationSeconds}s",
                                    color = Color(0xFF00F0FF),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Dialogue: ${current.dialogue.ifEmpty { "(No dialogue)" }}",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp
                        )

                        Text(
                            text = "Camera: ${current.cameraDirection}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        // Quick Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        if (current.durationSeconds > 2) {
                                            viewModel.updateScene(current.copy(durationSeconds = current.durationSeconds - 1))
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("1s", fontSize = 10.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (current.durationSeconds < 30) {
                                            viewModel.updateScene(current.copy(durationSeconds = current.durationSeconds + 1))
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("1s", fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = { viewModel.navigateTo(StudioScreen.SCENE_EDITOR) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color.White),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Edit Shot", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
