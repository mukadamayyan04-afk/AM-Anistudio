package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.StoryOutlineResult
import com.example.ui.viewmodel.AiGenerationState
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateAnimeScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var prompt by remember {
        mutableStateOf("In a neon-drenched megacity, a lone cyber-samurai protects an AI child whose mind holds the encryption keys to overthrow the corporate empire.")
    }
    var selectedGenre by remember { mutableStateOf("Cyberpunk Action") }
    var selectedArtStyle by remember { mutableStateOf("Cyberpunk 2077 Anime") }
    var targetDurationMins by remember { mutableIntStateOf(3) }

    val outlineState by viewModel.outlineState.collectAsState()
    val capabilityRouting by viewModel.capabilityRouting.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val textProviderId = capabilityRouting["TEXT"] ?: "gemini"
    val textProvider = providers.find { it.providerId == textProviderId }

    val genreList = listOf("Cyberpunk Action", "Shonen Battle", "Dark Fantasy", "Mecha Sci-Fi", "Supernatural Mystery")
    val styleList = listOf("Cyberpunk 2077 Anime", "Shonen Jump Dynamic", "Makoto Shinkai Cinematic", "Retro 90s Cel", "Ufotable VFX")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080C14))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF00F0FF),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "PROMPT-TO-ANIME WORKFLOW",
                            color = Color(0xFF00F0FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        color = Color(0xFF132238),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.clickable { viewModel.navigateTo(com.example.ui.viewmodel.StudioScreen.API_CONTROL_CENTER) }
                    ) {
                        Text(
                            text = "ENGINE: ${textProvider?.name ?: textProviderId.uppercase()}",
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
                    text = "Structured anime story generation powered by ${textProvider?.name ?: "Gemini"} (${textProvider?.modelId ?: "gemini-3.8-flash"}). Configure engines in API Control Center.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Prompt Input Field
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "STORY CONCEPT OR PREMISE",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Describe your anime premise, conflict, characters, or mood...", color = Color(0xFF475569)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00F0FF),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF0E1524),
                        unfocusedContainerColor = Color(0xFF0E1524),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        // Genre Picker
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "GENRE",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    genreList.forEach { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { selectedGenre = genre },
                            label = { Text(genre, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00F0FF),
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF101726),
                                labelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedGenre == genre,
                                borderColor = Color(0xFF1E293B),
                                selectedBorderColor = Color(0xFF00F0FF)
                            )
                        )
                    }
                }
            }
        }

        // Art Style Picker
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "VISUAL & ART STYLE",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styleList.forEach { style ->
                        FilterChip(
                            selected = selectedArtStyle == style,
                            onClick = { selectedArtStyle = style },
                            label = { Text(style, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF007A),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF101726),
                                labelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedArtStyle == style,
                                borderColor = Color(0xFF1E293B),
                                selectedBorderColor = Color(0xFFFF007A)
                            )
                        )
                    }
                }
            }
        }

        // Action Trigger
        item {
            Button(
                onClick = {
                    viewModel.generateStoryOutline(
                        prompt = prompt,
                        genre = selectedGenre,
                        artStyle = selectedArtStyle,
                        targetDurationMinutes = targetDurationMins
                    )
                },
                enabled = prompt.isNotBlank() && outlineState !is AiGenerationState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00F0FF),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (outlineState is AiGenerationState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Architecting Anime with Gemini 3.8 Flash...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Anime Story & Scenes", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Result State View
        item {
            when (val state = outlineState) {
                is AiGenerationState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF00F0FF), RoundedCornerShape(10.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF00F0FF))
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Orchestrating Narrative Breakdown...",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Calling gemini-3.8-flash for acts, characters, and keyframe prompts",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                is AiGenerationState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF261014), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFFF3366), RoundedCornerShape(10.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFF3366))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Generation Error", color = Color(0xFFFF3366), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(state.message, color = Color(0xFFF1F5F9), fontSize = 12.sp)
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(com.example.ui.viewmodel.StudioScreen.API_CONTROL_CENTER) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF), contentColor = Color.Black),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Open API Control Center", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                is AiGenerationState.Success -> {
                    val outline = state.data
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFF00F0FF), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                        text = "AI STORY ARCHITECTURE APPROVED",
                                        color = Color(0xFF00F0FF),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(18.dp))
                            }

                            Text(
                                text = outline.title,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )

                            Column {
                                Text("LOGLINE", color = Color(0xFF00F0FF), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(outline.logline, color = Color(0xFFE2E8F0), fontSize = 12.sp)
                            }

                            Column {
                                Text("THEMATIC CORE", color = Color(0xFFFF007A), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(outline.theme, color = Color(0xFFE2E8F0), fontSize = 12.sp)
                            }

                            Column {
                                Text("NARRATIVE ACTS", color = Color(0xFF9D4EDD), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(outline.actsSummary, color = Color(0xFFE2E8F0), fontSize = 12.sp)
                            }

                            Column {
                                Text("VISUAL DIRECTION", color = Color(0xFFFFD166), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(outline.visualStyleNotes, color = Color(0xFFE2E8F0), fontSize = 12.sp)
                            }

                            Spacer(Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    viewModel.applyAiGeneratedProject(
                                        outline = outline,
                                        artStyle = selectedArtStyle,
                                        genre = selectedGenre
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00F0FF),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Build Production Project & Timeline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                is AiGenerationState.Idle -> {
                    // Nothing to display
                }
            }
        }
    }
}
