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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StudioCanvasPlayer
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun HomeScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val activeProject by viewModel.activeProject.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val scenes by viewModel.scenes.collectAsState()
    val selectedScene by viewModel.selectedScene.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentTime by viewModel.currentTimeSeconds.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080C14))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Active Production Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF1E102E))
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0x3300F0FF),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ACTIVE PRODUCTION",
                                color = Color(0xFF00F0FF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "${scenes.size} SCENES READY",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = activeProject?.title ?: "No Active Project",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = activeProject?.description ?: "Create or select a project to begin anime production.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        maxLines = 2
                    )

                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.navigateTo(StudioScreen.CREATE_ANIME) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00F0FF),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("New Anime", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(StudioScreen.TIMELINE) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF334155)))),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.ViewTimeline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Timeline", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Live Studio Canvas Player Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REALTIME VIEWPORT",
                        color = Color(0xFF00F0FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "1080P // 24 FPS",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                StudioCanvasPlayer(
                    currentScene = selectedScene,
                    isPlaying = isPlaying,
                    currentTimeSeconds = currentTime,
                    totalDurationSeconds = viewModel.getTotalDurationSeconds(),
                    onTogglePlay = { viewModel.togglePlay() },
                    onSeek = { viewModel.seekTo(it) },
                    onPreviousScene = { viewModel.previousScene() },
                    onNextScene = { viewModel.nextScene() },
                    onSpeakDialogue = { viewModel.speakCurrentDialogue() }
                )
            }
        }

        // Quick Studio Pipelines Grid
        item {
            Text(
                text = "PRODUCTION ENGINES",
                color = Color(0xFF00F0FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Character Studio Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF101726), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .clickable { viewModel.navigateTo(StudioScreen.CHARACTER_STUDIO) }
                        .padding(12.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("3D Cast Studio", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Interactive 3D & 2D Sheets", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }

                // Scene Editor Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF101726), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .clickable { viewModel.navigateTo(StudioScreen.SCENE_EDITOR) }
                        .padding(12.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color(0xFFFF007A), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Scene Editor", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Shot Prompts & Directing", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }

                // Render Queue Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF101726), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .clickable { viewModel.navigateTo(StudioScreen.RENDER_QUEUE) }
                        .padding(12.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFFFD166), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Export Queue", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("MP4 & Video Render", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // API Control Center Direct Access Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1424), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .clickable { viewModel.navigateTo(StudioScreen.API_CONTROL_CENTER) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("API Control Center", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("13 Active AI Engines: Gemini, Claude, OpenAI, Runway, Tripo 3D", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                    }
                    Surface(color = Color(0xFF132238), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = "CONFIGURE",
                            color = Color(0xFF00F0FF),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Recent Projects Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STUDIO LIBRARY",
                    color = Color(0xFF00F0FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "View All (${projects.size})",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { viewModel.navigateTo(StudioScreen.PROJECTS) }
                )
            }
        }

        items(projects) { proj ->
            val isCurrent = proj.id == activeProject?.id
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isCurrent) Color(0xFF131F33) else Color(0xFF0E1420),
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (isCurrent) Color(0xFF00F0FF) else Color(0xFF1E293B),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.selectProject(proj) }
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
                                text = proj.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isCurrent) {
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF00F0FF), CircleShape)
                                )
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${proj.artStyle} • ${proj.aspectRatio} • ${proj.resolution}",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.selectProject(proj)
                            viewModel.navigateTo(StudioScreen.TIMELINE)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrent) Color(0xFF00F0FF) else Color(0xFF1E293B),
                            contentColor = if (isCurrent) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(if (isCurrent) "Open" else "Select", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
