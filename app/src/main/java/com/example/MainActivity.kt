package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.components.AllStudiosBottomSheet
import com.example.ui.components.StudioBottomBar
import com.example.ui.components.StudioTopBar
import com.example.ui.screens.AudioStudioScreen
import com.example.ui.screens.CharacterStudioScreen
import com.example.ui.screens.CreateAnimeScreen
import com.example.ui.screens.EpisodeStudioScreen
import com.example.ui.screens.ExportQueueScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.ProviderManagerScreen
import com.example.ui.screens.SceneEditorScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.theme.AMAniStudioTheme
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val activeProject by viewModel.activeProject.collectAsState()
            var showAllStudiosSheet by remember { mutableStateOf(false) }

            // Back handler: return to Home if currently on a sub-screen
            BackHandler(enabled = currentScreen != StudioScreen.HOME) {
                viewModel.navigateTo(StudioScreen.HOME)
            }

            AMAniStudioTheme(themeMode = themeMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        StudioTopBar(
                            currentScreen = currentScreen,
                            activeProject = activeProject,
                            onOpenNavSheet = { showAllStudiosSheet = true },
                            onOpenSettings = { viewModel.navigateTo(StudioScreen.SETTINGS) }
                        )
                    },
                    bottomBar = {
                        StudioBottomBar(
                            currentScreen = currentScreen,
                            onNavigate = { viewModel.navigateTo(it) },
                            onOpenAllStudios = { showAllStudiosSheet = true }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "studio_screen_transition"
                        ) { screen ->
                            when (screen) {
                                StudioScreen.HOME -> HomeScreen(viewModel = viewModel)
                                StudioScreen.CREATE_ANIME -> CreateAnimeScreen(viewModel = viewModel)
                                StudioScreen.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                                StudioScreen.CHARACTER_STUDIO -> CharacterStudioScreen(viewModel = viewModel)
                                StudioScreen.SCENE_EDITOR -> SceneEditorScreen(viewModel = viewModel)
                                StudioScreen.TIMELINE -> TimelineScreen(viewModel = viewModel)
                                StudioScreen.AUDIO_STUDIO -> AudioStudioScreen(viewModel = viewModel)
                                StudioScreen.EPISODE_STUDIO -> EpisodeStudioScreen(viewModel = viewModel)
                                StudioScreen.AI_PROVIDERS, StudioScreen.API_CONTROL_CENTER -> com.example.ui.screens.ApiControlCenterScreen(viewModel = viewModel)
                                StudioScreen.RENDER_QUEUE -> ExportQueueScreen(viewModel = viewModel)
                                StudioScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            }
                        }

                        if (showAllStudiosSheet) {
                            AllStudiosBottomSheet(
                                currentScreen = currentScreen,
                                onSelectScreen = { viewModel.navigateTo(it) },
                                onDismiss = { showAllStudiosSheet = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
