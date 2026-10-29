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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ViewInAr
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.CharacterEntity
import com.example.ui.components.Interactive3DViewport
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun CharacterStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val characters by viewModel.characters.collectAsState()
    val selectedCharacter by viewModel.selectedCharacter.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 3D Viewport, 1: 2D Sheet
    var showAddDialog by remember { mutableStateOf(false) }

    val capabilityRouting by viewModel.capabilityRouting.collectAsState()
    val prov3DId = capabilityRouting["ASSETS_3D"] ?: "tripo_ai"
    val prov3DMeta = com.example.data.ai.ProviderDirectory.getMetadata(prov3DId)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080C14))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title & Character Selector Strip
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CHARACTER STUDIO // 2D & 3D",
                            color = Color(0xFF00F0FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${characters.size} IN CAST • 3D ENGINE: ",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Text(
                                text = prov3DMeta?.name ?: prov3DId.uppercase(),
                                color = Color(0xFF9D4EDD),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { viewModel.navigateTo(com.example.ui.viewmodel.StudioScreen.API_CONTROL_CENTER) }
                            )
                        }
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
                        Text("Add Hero", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Character Horizontal Chip Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    characters.forEach { char ->
                        val isSelected = char.id == selectedCharacter?.id
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
                                .clickable { viewModel.selectCharacter(char) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            when (char.model3DType) {
                                                "CYBER_MECHA" -> Color(0xFFFF007A)
                                                "MAGICAL_GIRL" -> Color(0xFF9D4EDD)
                                                else -> Color(0xFF00F0FF)
                                            },
                                            CircleShape
                                        )
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = char.name,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // View Mode Tabs: 3D Viewport vs 2D Character Sheet
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0E1422),
                contentColor = Color(0xFF00F0FF),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF00F0FF)
                    )
                },
                modifier = Modifier.border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("3D Viewport Engine", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("2D Character Sheet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        // Tab Content
        val currentChar = selectedCharacter
        if (currentChar != null) {
            if (selectedTab == 0) {
                // 3D Viewport Tab
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Interactive3DViewport(
                            modelType = currentChar.model3DType
                        )

                        // Model Archetype Switcher
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("CYBER_WARRIOR" to "Cyber Warrior", "CYBER_MECHA" to "Titan Mech", "MAGICAL_GIRL" to "Mystic Girl").forEach { (typeKey, label) ->
                                val isSelected = currentChar.model3DType == typeKey
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateCharacter(currentChar.copy(model3DType = typeKey))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) Color(0xFF132238) else Color(0xFF0A0E17),
                                        contentColor = if (isSelected) Color(0xFF00F0FF) else Color(0xFF94A3B8)
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            if (isSelected) Color(0xFF00F0FF) else Color(0xFF1E293B)
                                        )
                                    )
                                ) {
                                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            } else {
                // 2D Character Profile & Specs Tab
                item {
                    var name by remember(currentChar.id) { mutableStateOf(currentChar.name) }
                    var role by remember(currentChar.id) { mutableStateOf(currentChar.role) }
                    var personality by remember(currentChar.id) { mutableStateOf(currentChar.personality) }
                    var promptDesc by remember(currentChar.id) { mutableStateOf(currentChar.visualPrompt) }
                    var outfit by remember(currentChar.id) { mutableStateOf(currentChar.outfitVariant) }
                    var expression by remember(currentChar.id) { mutableStateOf(currentChar.expressionNotes) }
                    var pitch by remember(currentChar.id) { mutableFloatStateOf(currentChar.voicePitch) }
                    var speed by remember(currentChar.id) { mutableFloatStateOf(currentChar.voiceSpeed) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0E1524), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "CHARACTER SPECIFICATIONS",
                            color = Color(0xFF00F0FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Hero Name") },
                                modifier = Modifier.weight(1f),
                                colors = outlinedColors()
                            )
                            OutlinedTextField(
                                value = role,
                                onValueChange = { role = it },
                                label = { Text("Role") },
                                modifier = Modifier.weight(1f),
                                colors = outlinedColors()
                            )
                        }

                        OutlinedTextField(
                            value = personality,
                            onValueChange = { personality = it },
                            label = { Text("Personality & Backstory") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlinedColors()
                        )

                        OutlinedTextField(
                            value = promptDesc,
                            onValueChange = { promptDesc = it },
                            label = { Text("Visual Anime Keyframe Prompt") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlinedColors()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = outfit,
                                onValueChange = { outfit = it },
                                label = { Text("Outfit Variant") },
                                modifier = Modifier.weight(1f),
                                colors = outlinedColors()
                            )
                            OutlinedTextField(
                                value = expression,
                                onValueChange = { expression = it },
                                label = { Text("Expression Notes") },
                                modifier = Modifier.weight(1f),
                                colors = outlinedColors()
                            )
                        }

                        // Voice Configuration
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF141C2E), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("VOICE SYNTHESIS SETTINGS", color = Color(0xFFFF007A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = {
                                        viewModel.ttsManager.speak(
                                            "My name is $name. ${personality.take(45)}",
                                            pitch = pitch,
                                            speed = speed
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007A)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Test Voice", fontSize = 10.sp)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Voice Pitch (${String.format("%.2f", pitch)})", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Slider(
                                value = pitch,
                                onValueChange = { pitch = it },
                                valueRange = 0.5f..1.8f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFFF007A), activeTrackColor = Color(0xFFFF007A))
                            )
                            Text("Voice Speed (${String.format("%.2f", speed)})", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Slider(
                                value = speed,
                                onValueChange = { speed = it },
                                valueRange = 0.5f..1.8f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFFF007A), activeTrackColor = Color(0xFFFF007A))
                            )
                        }

                        // Save & Delete Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.deleteCharacter(currentChar) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF3366))
                            }

                            Button(
                                onClick = {
                                    viewModel.updateCharacter(
                                        currentChar.copy(
                                            name = name,
                                            role = role,
                                            personality = personality,
                                            visualPrompt = promptDesc,
                                            outfitVariant = outfit,
                                            expressionNotes = expression,
                                            voicePitch = pitch,
                                            voiceSpeed = speed
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
                                Text("Save Character", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Character Dialog
    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newRole by remember { mutableStateOf("Protagonist") }
        var newPersonality by remember { mutableStateOf("") }
        var newPrompt by remember { mutableStateOf("") }
        var new3DType by remember { mutableStateOf("CYBER_WARRIOR") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF0D1424),
            title = { Text("Create Anime Character", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Hero Name") },
                        colors = outlinedColors()
                    )
                    OutlinedTextField(
                        value = newRole,
                        onValueChange = { newRole = it },
                        label = { Text("Role (Protagonist, Antagonist...)") },
                        colors = outlinedColors()
                    )
                    OutlinedTextField(
                        value = newPersonality,
                        onValueChange = { newPersonality = it },
                        label = { Text("Personality & Lore") },
                        colors = outlinedColors()
                    )
                    OutlinedTextField(
                        value = newPrompt,
                        onValueChange = { newPrompt = it },
                        label = { Text("Keyframe Appearance Prompt") },
                        colors = outlinedColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.addCharacter(
                                name = newName,
                                role = newRole,
                                personality = newPersonality,
                                visualPrompt = newPrompt,
                                model3DType = new3DType
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF), contentColor = Color.Black)
                ) {
                    Text("Add to Production")
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
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00F0FF),
    unfocusedBorderColor = Color(0xFF1E293B),
    focusedContainerColor = Color(0xFF080C14),
    unfocusedContainerColor = Color(0xFF080C14),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color(0xFF00F0FF),
    unfocusedLabelColor = Color(0xFF64748B)
)
