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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.StudioThemeMode
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun SettingsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    var isProMode by remember { mutableStateOf(true) }
    var hardwareAccel by remember { mutableStateOf(true) }
    var autoSave by remember { mutableStateOf(true) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "STUDIO SETTINGS & ENGINE",
                        color = Color(0xFF00F0FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Configure aesthetics, rendering pipelines, theme palettes, and AI orchestration presets.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Dedicated API Control Center Access Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1829), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF00F0FF), RoundedCornerShape(12.dp))
                    .clickable { viewModel.navigateTo(com.example.ui.viewmodel.StudioScreen.API_CONTROL_CENTER) }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF132238), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Tune,
                                contentDescription = null,
                                tint = Color(0xFF00F0FF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "API Control Center",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(6.dp))
                                Surface(color = Color(0x3300F0FF), shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        text = "13 ENGINES",
                                        color = Color(0xFF00F0FF),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Manage Gemini, OpenAI, Claude, Groq, Runway, ElevenLabs & Tripo 3D",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    androidx.compose.material3.Button(
                        onClick = { viewModel.navigateTo(com.example.ui.viewmodel.StudioScreen.API_CONTROL_CENTER) },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00F0FF),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Theme Palette Selection
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("STUDIO THEME PALETTE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(StudioThemeMode.CYBER_NEON, "Cyber Neon", Color(0xFF00F0FF)),
                            Triple(StudioThemeMode.ROYAL_GOLD, "Royal Gold", Color(0xFFFFD700)),
                            Triple(StudioThemeMode.MIDNIGHT_VIOLET, "Midnight Violet", Color(0xFFB026FF))
                        ).forEach { (mode, label, accent) ->
                            val isSelected = themeMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) Color(0xFF16233B) else Color(0xFF0A0E17), RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isSelected) accent else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setThemeMode(mode) }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(12.dp).background(accent, CircleShape))
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Studio Workflow Mode & Preferences
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFFFF007A), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("PRODUCTION PREFERENCES", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Professional Studio Mode", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Enables full 3D viewport shaders and multi-track sequencer", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Switch(
                            checked = isProMode,
                            onCheckedChange = { isProMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF132238))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Hardware Acceleration (GPU Canvas)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Optimized for Android phones & Redmi 13 5G", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Switch(
                            checked = hardwareAccel,
                            onCheckedChange = { hardwareAccel = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF132238))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Room Database Autosave", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Instant local persistence for characters, scripts, scenes", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                        Switch(
                            checked = autoSave,
                            onCheckedChange = { autoSave = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00F0FF), checkedTrackColor = Color(0xFF132238))
                        )
                    }
                }
            }
        }

        // Privacy & Security
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF06D6A0), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("DATA PRIVACY & FREE-FIRST POLICY", color = Color(0xFF06D6A0), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Text(
                        text = "• All story scripts, characters, scenes, and audio data are stored locally on your device in encrypted SQLite/Room.\n" +
                                "• Zero unauthorized tracking or telemetry.\n" +
                                "• Free-First architecture: Built-in Native Canvas Media Engine and Android Neural TTS are 100% free with unlimited local renders.\n" +
                                "• Gemini AI uses Google free tier / developer quotas via secure BuildConfig injection.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // System Specs
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF060910), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "AM ANISTUDIO v1.0.0-PRO • ENGINE: JETPACK COMPOSE 3D // KOTLIN 2.2 • TARGET: ANDROID 14/15",
                    color = Color(0xFF475569),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
