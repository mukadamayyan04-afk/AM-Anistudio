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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.ProjectEntity
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ProjectsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

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
                        Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "PROJECT ARCHIVE",
                            color = Color(0xFF00F0FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "${projects.size} ANIME PRODUCTIONS CATALOGUED",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("New Project", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(projects) { proj ->
            val isCurrent = proj.id == activeProject?.id
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isCurrent) Color(0xFF131F33) else Color(0xFF0E1420),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        if (isCurrent) Color(0xFF00F0FF) else Color(0xFF1E293B),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { viewModel.selectProject(proj) }
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = proj.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isCurrent) {
                                Spacer(Modifier.width(6.dp))
                                Surface(color = Color(0x3300F0FF), shape = RoundedCornerShape(4.dp)) {
                                    Text("ACTIVE", color = Color(0xFF00F0FF), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteProject(proj) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF3366), modifier = Modifier.size(16.dp))
                        }
                    }

                    Text(
                        text = proj.description.ifEmpty { "No description entered." },
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        maxLines = 2
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${proj.artStyle} • ${proj.aspectRatio} • ${proj.resolution} • ${proj.frameRate}fps",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

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
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(if (isCurrent) "Open Timeline" else "Select Project", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var style by remember { mutableStateOf("Cyberpunk 2077 Anime") }
        var aspect by remember { mutableStateOf("16:9") }
        var res by remember { mutableStateOf("1080p (FHD)") }
        var durationSecs by remember { mutableIntStateOf(180) }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = Color(0xFF0D1424),
            title = { Text("New Anime Project", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Project Title") },
                        colors = projectFieldColors()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Logline / Description") },
                        colors = projectFieldColors()
                    )
                    OutlinedTextField(
                        value = style,
                        onValueChange = { style = it },
                        label = { Text("Art Style") },
                        colors = projectFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.createProject(
                                title = title,
                                description = desc,
                                artStyle = style,
                                aspectRatio = aspect,
                                resolution = res,
                                durationSeconds = durationSecs
                            )
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF), contentColor = Color.Black)
                ) {
                    Text("Create Project")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun projectFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00F0FF),
    unfocusedBorderColor = Color(0xFF1E293B),
    focusedContainerColor = Color(0xFF080C14),
    unfocusedContainerColor = Color(0xFF080C14),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
