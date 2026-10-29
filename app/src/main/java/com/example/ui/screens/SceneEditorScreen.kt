package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun SceneEditorScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val scenes by viewModel.scenes.collectAsState()
    val selectedScene by viewModel.selectedScene.collectAsState()
    val characters by viewModel.characters.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

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
                    Text(
                        text = "SCENE DIRECTOR // SHOT PLANNER",
                        color = Color(0xFF00F0FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${scenes.size} SCENES IN SEQUENCE",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00F0FF),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("New Shot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Horizontal Scene Chips Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scenes.forEachIndexed { index, scene ->
                    val isSelected = scene.id == selectedScene?.id
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFF132238) else Color(0xFF101624),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF00F0FF) else Color(0xFF1E293B),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectScene(scene) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${index + 1}",
                                color = Color(scene.accentColor),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = scene.title,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Selected Scene Editor Form
        val current = selectedScene
        if (current != null) {
            item {
                var title by remember(current.id) { mutableStateOf(current.title) }
                var visualPrompt by remember(current.id) { mutableStateOf(current.visualPrompt) }
                var speaker by remember(current.id) { mutableStateOf(current.speakerName) }
                var dialogue by remember(current.id) { mutableStateOf(current.dialogue) }
                var cameraDir by remember(current.id) { mutableStateOf(current.cameraDirection) }
                var duration by remember(current.id) { mutableIntStateOf(current.durationSeconds) }
                var bgm by remember(current.id) { mutableStateOf(current.bgmTrack) }
                var sfx by remember(current.id) { mutableStateOf(current.soundEffect) }
                var status by remember(current.id) { mutableStateOf(current.status) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0E1524), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SHOT PARAMETERS // SCENE ${current.sceneIndex + 1}",
                            color = Color(0xFF00F0FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = status,
                                color = Color(0xFFFFD166),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Scene Title / Location") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = sceneFieldColors()
                    )

                    OutlinedTextField(
                        value = visualPrompt,
                        onValueChange = { visualPrompt = it },
                        label = { Text("Visual Prompt & Cel Shading Directives") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = sceneFieldColors()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = speaker,
                            onValueChange = { speaker = it },
                            label = { Text("Speaker Character") },
                            modifier = Modifier.weight(1f),
                            colors = sceneFieldColors()
                        )
                        OutlinedTextField(
                            value = cameraDir,
                            onValueChange = { cameraDir = it },
                            label = { Text("Camera Direction / Angle") },
                            modifier = Modifier.weight(1f),
                            colors = sceneFieldColors()
                        )
                    }

                    // Dialogue Line
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF141C2E), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("DIALOGUE & VOICE SYNTHESIS", color = Color(0xFFFF007A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = {
                                    if (dialogue.isNotBlank()) {
                                        val char = characters.find { it.name == speaker }
                                        viewModel.ttsManager.speak(
                                            dialogue,
                                            pitch = char?.voicePitch ?: 1.0f,
                                            speed = char?.voiceSpeed ?: 1.0f
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007A)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Speak", fontSize = 10.sp)
                            }
                        }

                        OutlinedTextField(
                            value = dialogue,
                            onValueChange = { dialogue = it },
                            placeholder = { Text("Enter character spoken line...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = sceneFieldColors()
                        )
                    }

                    // Duration Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Shot Duration", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("${duration}s (${duration * 24} frames)", color = Color(0xFF00F0FF), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = duration.toFloat(),
                            onValueChange = { duration = it.toInt() },
                            valueRange = 2f..20f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF00F0FF),
                                activeTrackColor = Color(0xFF00F0FF)
                            )
                        )
                    }

                    // Audio & SFX
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bgm,
                            onValueChange = { bgm = it },
                            label = { Text("BGM Soundtrack") },
                            modifier = Modifier.weight(1f),
                            colors = sceneFieldColors()
                        )
                        OutlinedTextField(
                            value = sfx,
                            onValueChange = { sfx = it },
                            label = { Text("Sound Effect (SFX)") },
                            modifier = Modifier.weight(1f),
                            colors = sceneFieldColors()
                        )
                    }

                    // Save / Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.deleteScene(current) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF3366))
                        }

                        Button(
                            onClick = {
                                viewModel.updateScene(
                                    current.copy(
                                        title = title,
                                        visualPrompt = visualPrompt,
                                        speakerName = speaker,
                                        dialogue = dialogue,
                                        cameraDirection = cameraDir,
                                        durationSeconds = duration,
                                        bgmTrack = bgm,
                                        soundEffect = sfx,
                                        status = status
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00F0FF),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Save Scene Directives", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add Scene Dialog
    if (showAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newPrompt by remember { mutableStateOf("") }
        var newSpeaker by remember { mutableStateOf("") }
        var newDialogue by remember { mutableStateOf("") }
        var newDuration by remember { mutableIntStateOf(5) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF0D1424),
            title = { Text("Add Anime Scene", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Scene Title") },
                        colors = sceneFieldColors()
                    )
                    OutlinedTextField(
                        value = newPrompt,
                        onValueChange = { newPrompt = it },
                        label = { Text("Visual Prompt") },
                        colors = sceneFieldColors()
                    )
                    OutlinedTextField(
                        value = newSpeaker,
                        onValueChange = { newSpeaker = it },
                        label = { Text("Speaker") },
                        colors = sceneFieldColors()
                    )
                    OutlinedTextField(
                        value = newDialogue,
                        onValueChange = { newDialogue = it },
                        label = { Text("Dialogue") },
                        colors = sceneFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addScene(
                                title = newTitle,
                                visualPrompt = newPrompt,
                                dialogue = newDialogue,
                                speaker = newSpeaker,
                                duration = newDuration
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF), contentColor = Color.Black)
                ) {
                    Text("Add Scene")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun sceneFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00F0FF),
    unfocusedBorderColor = Color(0xFF1E293B),
    focusedContainerColor = Color(0xFF080C14),
    unfocusedContainerColor = Color(0xFF080C14),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color(0xFF00F0FF),
    unfocusedLabelColor = Color(0xFF64748B)
)
