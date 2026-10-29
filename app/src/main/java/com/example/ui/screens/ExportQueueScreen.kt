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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ProviderDirectory
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ExportQueueScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val renderJobs by viewModel.renderJobs.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val scenes by viewModel.scenes.collectAsState()
    val capabilityRouting by viewModel.capabilityRouting.collectAsState()
    val videoProviderId = capabilityRouting["VIDEO"] ?: "runway"
    val videoProvider = ProviderDirectory.findById(videoProviderId)

    var selectedFormat by remember { mutableStateOf("MP4 (H.264)") }
    var selectedRes by remember { mutableStateOf("1920x1080 (FHD)") }
    var selectedFps by remember { mutableIntStateOf(24) }

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
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFFFFD166), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "EXPORT & RENDER QUEUE",
                            color = Color(0xFFFFD166),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Surface(
                        color = Color(0xFF00F0FF).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.clickable { viewModel.navigateTo(StudioScreen.API_CONTROL_CENTER) }
                    ) {
                        Text(
                            text = "ENGINE: ${videoProvider?.name ?: videoProviderId.uppercase()}",
                            color = Color(0xFF00F0FF),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Package multi-track anime scenes, 3D perspective frames, voice tracks, and subtitles into master video files.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Render Trigger Panel
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF101726), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .clickable { viewModel.navigateTo(StudioScreen.API_CONTROL_CENTER) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE VIDEO GENERATION ENGINE",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${videoProvider?.name ?: "Runway"} • Model: ${videoProvider?.verifiedModels?.firstOrNull() ?: "gen-3-alpha"}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "CHANGE ›",
                            color = Color(0xFFFFD166),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "NEW MASTER RENDER DISPATCH",
                        color = Color(0xFF00F0FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = "Target: ${activeProject?.title ?: "Current Project"} (${scenes.size} scenes, ~${viewModel.getTotalDurationSeconds().toInt()}s)",
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    // Format Selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("CONTAINER FORMAT", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("MP4 (H.264)", "Animated GIF", "WebM").forEach { fmt ->
                                FilterChip(
                                    selected = selectedFormat == fmt,
                                    onClick = { selectedFormat = fmt },
                                    label = { Text(fmt, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFFD166),
                                        selectedLabelColor = Color.Black,
                                        containerColor = Color(0xFF101726),
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Resolution Selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("RESOLUTION", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("1920x1080 (FHD)", "1280x720 (HD)", "3840x2160 (4K)").forEach { res ->
                                FilterChip(
                                    selected = selectedRes == res,
                                    onClick = { selectedRes = res },
                                    label = { Text(res, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF00F0FF),
                                        selectedLabelColor = Color.Black,
                                        containerColor = Color(0xFF101726),
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Frame Rate
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("TIMELINE FRAME RATE", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(24 to "24 FPS (Anime Standard)", 30 to "30 FPS", 60 to "60 FPS").forEach { (fpsVal, label) ->
                                FilterChip(
                                    selected = selectedFps == fpsVal,
                                    onClick = { selectedFps = fpsVal },
                                    label = { Text(label, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFF007A),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF101726),
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.queueRenderJob(
                                format = selectedFormat,
                                resolution = selectedRes,
                                fps = selectedFps
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD166),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Queue Master Video Render", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Jobs Queue
        item {
            Text(
                text = "RENDER PIPELINE QUEUE (${renderJobs.size})",
                color = Color(0xFF00F0FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        if (renderJobs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0E1422), RoundedCornerShape(10.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No rendering jobs in queue. Dispatch a render above.", color = Color(0xFF64748B), fontSize = 12.sp)
                }
            }
        }

        items(renderJobs) { job ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1424), RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (job.status == "COMPLETED") Color(0xFF06D6A0) else Color(0xFF1E293B),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(job.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Format: ${job.format} • ${job.resolution} • ${job.fps}fps",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (job.status == "COMPLETED") Color(0x3306D6A0) else Color(0x33FFD166),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${job.status} [${job.progress}%]",
                                    color = if (job.status == "COMPLETED") Color(0xFF06D6A0) else Color(0xFFFFD166),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            IconButton(
                                onClick = { viewModel.deleteRenderJob(job.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Job", tint = Color(0xFFFF3366), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = { job.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = if (job.status == "COMPLETED") Color(0xFF06D6A0) else Color(0xFF00F0FF),
                        trackColor = Color(0xFF1E293B)
                    )

                    // Compositor Log Output Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF060910), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = job.logText,
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (job.outputUri != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Output: ${job.outputUri}",
                                color = Color(0xFF06D6A0),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.ttsManager.speak("Master render completed. Video packaged successfully.") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06D6A0), contentColor = Color.Black),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Play Verify", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
