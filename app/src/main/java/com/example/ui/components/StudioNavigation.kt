package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.viewmodel.StudioScreen
import kotlinx.coroutines.launch

data class StudioNavDestination(
    val screen: StudioScreen,
    val icon: ImageVector,
    val label: String,
    val subtitle: String
)

val AllDestinations = listOf(
    StudioNavDestination(StudioScreen.HOME, Icons.Default.Home, "Home", "Dashboard & metrics"),
    StudioNavDestination(StudioScreen.CREATE_ANIME, Icons.Default.AutoAwesome, "Create Anime", "AI prompt to production"),
    StudioNavDestination(StudioScreen.PROJECTS, Icons.Default.Folder, "Projects", "Library & versions"),
    StudioNavDestination(StudioScreen.CHARACTER_STUDIO, Icons.Default.Person, "Characters", "2D sheets & 3D viewport"),
    StudioNavDestination(StudioScreen.SCENE_EDITOR, Icons.Default.Movie, "Scene Editor", "Shots & camera angles"),
    StudioNavDestination(StudioScreen.TIMELINE, Icons.Default.ViewTimeline, "Timeline", "Multi-track sequencer"),
    StudioNavDestination(StudioScreen.AUDIO_STUDIO, Icons.Default.Headphones, "Audio & Voice", "TTS & dialogue tracks"),
    StudioNavDestination(StudioScreen.EPISODE_STUDIO, Icons.Default.Tv, "Episodes", "Season arcs & continuity"),
    StudioNavDestination(StudioScreen.API_CONTROL_CENTER, Icons.Default.CloudQueue, "API Control Center", "13 AI provider engines"),
    StudioNavDestination(StudioScreen.RENDER_QUEUE, Icons.Default.Speed, "Render Queue", "Export & encoding jobs"),
    StudioNavDestination(StudioScreen.SETTINGS, Icons.Default.Settings, "Settings", "Themes & preferences")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioTopBar(
    currentScreen: StudioScreen,
    activeProject: ProjectEntity?,
    onOpenNavSheet: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF00F0FF), CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AM ANISTUDIO",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFF1F5F9),
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = Color(0xFF00F0FF),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = activeProject?.title ?: currentScreen.title,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onOpenNavSheet) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "All Studios Menu",
                    tint = Color(0xFF00F0FF)
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color(0xFF94A3B8)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF070B12),
            titleContentColor = Color.White
        ),
        modifier = modifier.border(0.5.dp, Color(0xFF1E293B))
    )
}

@Composable
fun StudioBottomBar(
    currentScreen: StudioScreen,
    onNavigate: (StudioScreen) -> Unit,
    onOpenAllStudios: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickDestinations = listOf(
        StudioNavDestination(StudioScreen.HOME, Icons.Default.Home, "Home", ""),
        StudioNavDestination(StudioScreen.CREATE_ANIME, Icons.Default.AutoAwesome, "Create", ""),
        StudioNavDestination(StudioScreen.TIMELINE, Icons.Default.ViewTimeline, "Timeline", ""),
        StudioNavDestination(StudioScreen.CHARACTER_STUDIO, Icons.Default.Person, "3D Cast", "")
    )

    NavigationBar(
        containerColor = Color(0xFF080C14),
        contentColor = Color(0xFF94A3B8),
        modifier = modifier
            .border(0.5.dp, Color(0xFF1E293B))
            .navigationBarsPadding()
    ) {
        quickDestinations.forEach { dest ->
            val isSelected = currentScreen == dest.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(dest.screen) },
                icon = {
                    Icon(
                        imageVector = dest.icon,
                        contentDescription = dest.label,
                        tint = if (isSelected) Color(0xFF00F0FF) else Color(0xFF64748B)
                    )
                },
                label = {
                    Text(
                        text = dest.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF00F0FF) else Color(0xFF64748B)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF132238)
                )
            )
        }

        // More Studios Navigation Item
        val isMoreActive = currentScreen !in quickDestinations.map { it.screen }
        NavigationBarItem(
            selected = isMoreActive,
            onClick = onOpenAllStudios,
            icon = {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "More Studios",
                    tint = if (isMoreActive) Color(0xFFFF007A) else Color(0xFF64748B)
                )
            },
            label = {
                Text(
                    text = "Studios",
                    fontSize = 10.sp,
                    fontWeight = if (isMoreActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isMoreActive) Color(0xFFFF007A) else Color(0xFF64748B)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFF331124)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllStudiosBottomSheet(
    currentScreen: StudioScreen,
    onSelectScreen: (StudioScreen) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0B101C),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFF334155), CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AM ANISTUDIO SUITE",
                    color = Color(0xFF00F0FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "11 ENGINES",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(AllDestinations) { dest ->
                    val isSelected = currentScreen == dest.screen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFF132238) else Color(0xFF111827),
                                RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF00F0FF) else Color(0xFF1E293B),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onSelectScreen(dest.screen)
                                onDismiss()
                            }
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = dest.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF00F0FF) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(24.dp)
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFF00F0FF), CircleShape)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = dest.label,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dest.subtitle,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
